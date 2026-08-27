-- Food Courier Dispatch System — SQLite Schema
-- Run with: sqlite3 food_courier.db < database/schema.sql

PRAGMA foreign_keys = ON;

-- ============================================================
-- LOCATIONS
-- ============================================================
CREATE TABLE locations (
    id          INTEGER PRIMARY KEY,
    name        TEXT NOT NULL,
    latitude    REAL NOT NULL,
    longitude   REAL NOT NULL,
    type        TEXT NOT NULL CHECK (type IN ('CAMPUS', 'ROAD', 'OTHER', 'RESTAURANT', 'CUSTOMER'))
);

-- ============================================================
-- ROADS (edges in the delivery graph)
-- ============================================================
CREATE TABLE roads (
    id                  INTEGER PRIMARY KEY,
    from_location_id    INTEGER NOT NULL,
    to_location_id      INTEGER NOT NULL,
    distance_km         REAL NOT NULL CHECK (distance_km > 0),
    travel_time_min     INTEGER NOT NULL CHECK (travel_time_min > 0),
    is_bidirectional    INTEGER NOT NULL DEFAULT 1 CHECK (is_bidirectional IN (0, 1)),
    FOREIGN KEY (from_location_id) REFERENCES locations(id),
    FOREIGN KEY (to_location_id)   REFERENCES locations(id)
);

-- ============================================================
-- CUSTOMERS
-- ============================================================
CREATE TABLE customers (
    id           INTEGER PRIMARY KEY,
    name         TEXT NOT NULL,
    phone        TEXT NOT NULL,
    location_id  INTEGER NOT NULL,
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

-- ============================================================
-- RESTAURANTS
-- ============================================================
CREATE TABLE restaurants (
    id           INTEGER PRIMARY KEY,
    name         TEXT NOT NULL,
    phone        TEXT NOT NULL,
    location_id  INTEGER NOT NULL,
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

-- ============================================================
-- COURIERS
-- ============================================================
CREATE TABLE couriers (
    id                  INTEGER PRIMARY KEY,
    name                TEXT NOT NULL,
    phone               TEXT NOT NULL,
    status              TEXT NOT NULL DEFAULT 'AVAILABLE'
                        CHECK (status IN ('AVAILABLE', 'BUSY', 'OFFLINE')),
    current_location_id INTEGER,
    FOREIGN KEY (current_location_id) REFERENCES locations(id)
);

-- ============================================================
-- ORDERS
-- ============================================================
CREATE TABLE orders (
    id                          INTEGER PRIMARY KEY,
    customer_id                 INTEGER NOT NULL,
    restaurant_id               INTEGER NOT NULL,
    order_time                  TEXT NOT NULL,          -- ISO-8601: 2026-08-13T12:00:00
    priority                    INTEGER NOT NULL CHECK (priority BETWEEN 1 AND 4),
    status                      TEXT NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING', 'PREPARING', 'DISPATCHED', 'DELIVERED', 'CANCELLED')),
    estimated_prep_minutes      INTEGER NOT NULL DEFAULT 15,
    FOREIGN KEY (customer_id)   REFERENCES customers(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- ============================================================
-- DELIVERIES (completed delivery records for Alpha 5 reporting)
-- ============================================================
CREATE TABLE deliveries (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id                INTEGER NOT NULL UNIQUE,
    courier_id              INTEGER NOT NULL,
    distance_km             REAL NOT NULL CHECK (distance_km >= 0),
    estimated_time_min      INTEGER NOT NULL CHECK (estimated_time_min >= 0),
    actual_time_min         INTEGER,                  -- filled in after completion
    status                  TEXT NOT NULL DEFAULT 'COMPLETED'
                        CHECK (status IN ('COMPLETED', 'FAILED', 'PARTIAL')),
    created_at              TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (order_id)   REFERENCES orders(id),
    FOREIGN KEY (courier_id) REFERENCES couriers(id)
);

-- ============================================================
-- INDEXES for common query patterns
-- ============================================================
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_restaurant ON orders(restaurant_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_time ON orders(order_time);

CREATE INDEX idx_deliveries_courier ON deliveries(courier_id);
CREATE INDEX idx_deliveries_created ON deliveries(created_at);

CREATE INDEX idx_roads_from ON roads(from_location_id);
CREATE INDEX idx_roads_to ON roads(to_location_id);

-- ============================================================
-- VIEWS for reporting (Alpha 5)
-- ============================================================
CREATE VIEW v_courier_performance AS
SELECT
    c.id AS courier_id,
    c.name AS courier_name,
    COUNT(d.id) AS delivery_count,
    ROUND(SUM(d.distance_km), 2) AS total_distance_km,
    SUM(d.estimated_time_min) AS total_time_min,
    ROUND(AVG(d.estimated_time_min), 2) AS avg_time_min
FROM couriers c
LEFT JOIN deliveries d ON d.courier_id = c.id
GROUP BY c.id, c.name
ORDER BY delivery_count DESC;

CREATE VIEW v_daily_summary AS
SELECT
    date(created_at) AS day,
    COUNT(*) AS deliveries,
    ROUND(SUM(distance_km), 2) AS total_km,
    ROUND(AVG(estimated_time_min), 2) AS avg_min
FROM deliveries
GROUP BY date(created_at)
ORDER BY day DESC;