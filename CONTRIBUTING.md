# Tasks Prerequisite & Git Workflow
 
The five Alpha branches (`alpha1`, `alpha2`, `alpha3`, `alpha4`, `alpha5`) already exist off `main`. Everyone works inside their own Alpha branch, and inside a personal branch off that — never commit straight to your Alpha branch or to `main`.
 
### **One-time setup:**
- [ ] `git fetch origin`
- [ ] `git checkout alphaN` (your team's branch, e.g. `git checkout alpha1`)
- [ ] `git pull origin alphaN` — make sure you have the latest before branching off it


### **Create your personal task branch (naming: `firstname-b`, e.g. `joana-b`):**
- [ ] `git checkout -b joana-b` (run this while on `alphaN`, so it branches off your team's branch, not `main`)
- [ ] Push it once so it exists on the remote: `git push -u origin joana-b`


### **While working — pull in your teammates' progress often, to avoid a pile-up of conflicts at the end:**
- [ ] `git fetch origin`
- [ ] `git merge origin/alphaN` (while on your personal branch) — do this at the start of each work session, not just before your PR
- [ ] Resolve any conflicts locally and re-run your tests before continuing
- [ ] Commit small and often, with clear messages (e.g. `feat(alpha1): implement ArrayQueue enqueue/dequeue`)


### **When your checklist is done:**
- [ ] `git push origin joana-b`
- [ ] Open a PR: `joana-b` → `alphaN` (not into `main` — that merge happens once at Alpha level after the whole team's branches are integrated)
- [ ] Ask one teammate on your Alpha to review before merging
- [ ] After merging, delete the personal branch to keep things tidy: `git branch -d joana-b` and `git push origin --delete joana-b`


### **Avoiding conflicts on shared files (interfaces, `domain/` classes, `pom.xml`):**
- [ ] Never edit a file someone else owns (see the single-owner rule in the respective tasks) — if you need a change to a shared interface, flag it in the team channel first, don't just edit it
- [ ] Pull/merge `alphaN` into your personal branch *before* you start a session, not only right before opening your PR — small frequent merges are far easier to resolve than one big one
- [ ] If a conflict does appear, resolve it locally (`git status` shows the conflicted files), test again, then commit the merge — don't force-push over a conflict
