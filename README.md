# 🍔 Food Courier Dispatch System

A Java-based Food Courier Dispatch System developed as a joint Data Structures and Algorithms project.

The project demonstrates how fundamental data structures and algorithms can be combined to solve a realistic food-delivery dispatch problem.

---

## 🎯 Project Goal

The system models the process of receiving food orders, prioritizing deliveries, assigning couriers, navigating a delivery network, and optimizing delivery decisions.

The project is intentionally built around **custom implementations** of core data structures and algorithms rather than relying on Java's built-in implementations for the assessed components.

---

## 🏗️ System Architecture

The system is divided into five functional areas called **"Alphas"**.

```text
                   GENERATED DATA
                         │
                         ▼
                ┌─────────────────┐
                │ A1              │
                │ Order Intake    │
                │ Queue / List    │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │ A2              │
                │ Priority        │
                │ Dispatch        │
                │ Heap / PQ       │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │ A3              │
                │ Courier         │
                │ Assignment      │
                │ Hash / BST      │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │ A4              │
                │ Route &         │
                │ Navigation      │
                │ Graph           │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │ A5              │
                │ Optimization &  │
                │ Reporting       │
                └─────────────────┘
```

---

## 📁 Project Structure

```text
food-courier-dispatch/
│
├── README.md
├── pom.xml
├── .gitignore
│
├── data/
│   ├── seed/
│   └── generated/
│
├── database/
│   └── schema.sql
│
├── docs/
│   ├── architecture/
│   ├── decisions/
│   └── dsa/
│
├── experiments/
│   └── results/
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/foodcourier/
    │   │       │
    │   │       ├── domain/
    │   │       │
    │   │       ├── dsa/
    │   │       │   ├── disjointset/
    │   │       │   ├── graph/
    │   │       │   ├── hashtable/
    │   │       │   ├── heap/
    │   │       │   ├── list/
    │   │       │   ├── queue/
    │   │       │   └── tree/
    │   │       │
    │   │       ├── algorithms/
    │   │       │   ├── graph/
    │   │       │   ├── optimization/
    │   │       │   ├── searching/
    │   │       │   └── sorting/
    │   │       │
    │   │       ├── alpha1/
    │   │       ├── alpha2/
    │   │       ├── alpha3/
    │   │       ├── alpha4/
    │   │       ├── alpha5/
    │   │       │
    │   │       ├── repository/
    │   │       └── app/
    │   │
    │   └── resources/
    │
    └── test/
        └── java/
            └── com/foodcourier/
                ├── algorithms/
                ├── alpha1/
                ├── alpha2/
                ├── alpha3/
                ├── alpha4/
                ├── alpha5/
                └── dsa/
```

---

## 🧩 Major Components

### Domain

Contains the objects that represent the food delivery system.

Examples:
- `Order`
- `Courier`
- `Customer`
- `Restaurant`
- `Location`
- `Road`
- `Delivery`

### Data Structures

Contains custom implementations of the data structures used by the project.

Examples:
- Queue
- Linked List
- Heap
- Hash Table
- Binary Search Tree
- Graph
- Disjoint Set

### Algorithms

Contains implementations of the algorithms required by the project.

**Searching**
- Linear Search
- Binary Search

**Sorting**
- Merge Sort
- Quick Sort

**Graph Algorithms**
- BFS
- DFS
- Dijkstra
- Prim
- Kruskal

**Optimization**
- Greedy
- Dynamic Programming

---

## 🔵 Alpha 1 — Order Intake

**Question:** How do we receive and process orders?

**Data Structures**
- Queue
- Linked List

**Algorithms**
- Linear Search
- Sorting

## 🟣 Alpha 2 — Priority Dispatch

**Question:** Which order should be dispatched next?

**Data Structures**
- Priority Queue
- Heap

**Algorithms**
- Searching
- Sorting where appropriate

## 🟢 Alpha 3 — Courier Assignment

**Question:** How do we find an appropriate courier?

**Data Structures**
- Hash Table
- Binary Search Tree

**Algorithms**
- Linear Search
- Binary Search

## 🟠 Alpha 4 — Route & Navigation

**Question:** How do we navigate the delivery network?

**Data Structures**
- Graph
- Heap / Priority Queue
- Disjoint Set

**Algorithms**
- BFS
- DFS
- Dijkstra
- Prim
- Kruskal

## 🔴 Alpha 5 — Optimization & Reporting

**Question:** How do we make the best delivery decisions under constraints?

**Data Structures**
- Graph
- Heap
- Disjoint Set

**Algorithms**
- Greedy
- Dynamic Programming
- Sorting

---

## 🔄 Component Communication

The Alpha components communicate through shared domain objects and clearly defined service contracts.

```text
Order
  │
  ▼
A1 Order Intake
  │
  │ Order
  ▼
A2 Priority Dispatch
  │
  │ Selected Order
  ▼
A3 Courier Assignment
  │
  │ Order + Courier
  ▼
A4 Navigation
  │
  │ Route
  ▼
A5 Optimization
  │
  ▼
Delivery Result / Report
```

---

## 🧱 Architectural Principle

The project separates:

```text
Domain
   ↓
Data Structures
   ↓
Algorithms
   ↓
Application Services
   ↓
System Orchestration
```

Data structures and algorithms should **not** be tightly coupled to a specific Alpha.

For example:

```text
dsa/heap/MinHeap.java
          │
          ▼
alpha2/PriorityDispatchService.java
```

rather than implementing the heap directly inside Alpha 2.

---

## 🧪 Testing

Every major data structure, algorithm, and Alpha service should have corresponding tests under:

```text
src/test/java/com/foodcourier/
```

Testing should cover:
- Normal cases
- Empty structures
- Boundary cases
- Invalid input where applicable
- Algorithm correctness
- Performance where required

---

## 📊 Performance Experiments

Performance experiments will be stored in:

```text
experiments/results/
```

The project will eventually compare performance using different input sizes, for example:
- 100 orders
- 1,000 orders
- 10,000 orders
- 100,000 orders

Measurements may include:
- Execution time
- Memory usage
- Number of operations
- Growth behaviour

---

## 💾 Data Strategy

During initial development, the project uses generated dummy data.

```text
data/
├── seed/
└── generated/
```

Real-world data collection and database integration will be introduced after the core system has been implemented and tested.

---

## 🚫 Development Rules

1. Do not place data structure implementations inside Alpha folders.
2. Do not duplicate domain classes.
3. Do not modify another Alpha's code without coordination.
4. Test implementations before integration.
5. Document algorithm complexity.
6. Use the project's custom implementations for assessed DSA components.
7. Keep Alpha responsibilities clearly separated.
8. Prefer simple, understandable solutions before optimization.

---

## 🚀 Development Workflow

```text
Study
  ↓
Implement DSA
  ↓
Test DSA
  ↓
Implement Algorithm
  ↓
Test Algorithm
  ↓
Build Alpha Service
  ↓
Test with Dummy Data
  ↓
Integrate
  ↓
Benchmark
  ↓
Real Data
```

---

## 👥 Team Structure

The project is divided into five Alpha teams.

Each team owns its Alpha's application logic while collaborating on shared domain models, data structures, algorithms, and integration contracts.

The Project Manager coordinates the interfaces between the Alpha components and oversees system integration.

---

## 📌 Current Development Principle

Build the system with controlled generated data first. Make it correct, test it, measure it, and only then introduce real-world data.