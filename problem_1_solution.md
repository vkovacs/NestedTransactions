The issue occurs because of **transaction isolation and persistence context behavior** in Spring and Hibernate.

---

### 🔍 **Understanding the Problem**
1. The `processUser()` method in `UserService` runs **inside a transaction** (`@Transactional`).
2. Inside `processUser()`, `userRepository.findById(id)` retrieves the `UserEntity` and caches it in the **current persistence context**.
3. The `renameUser()` method in `UserPersistenceService` has `@Transactional(propagation = Propagation.REQUIRES_NEW)`, meaning:
    - A **new, separate transaction** is started for renaming.
    - The renaming operation (`setName()` and `save()`) commits **immediately** to the database.
    - The new transaction ends.
4. After `renameUser()` returns, `processUser()` **still operates within the original transaction**.
5. When `userRepository.findById(id)` is called **again** in `processUser()`, Hibernate **does not query the database** but instead returns the entity from its current persistence context.
    - Since this context was created **before the rename**, it still holds the old version of the entity.
    - The changes made in the `REQUIRES_NEW` transaction **are not visible within the same persistence context** of `processUser()`.

---

### 🔥 **Why Doesn't It See the Renamed User?**
Spring and Hibernate use **first-level cache** (a.k.a. persistence context) for managing entities within a transaction. Since `processUser()` started first:
- The entity was **loaded into the persistence context before renaming**.
- The `findById()` call after renaming **returns the cached version** instead of fetching from the database.

---

### ✅ **How to Fix It**
#### 1️⃣ **Clear Persistence Context Before Re-fetching**
Manually clear the persistence context in `processUser()` to force a fresh database read:
```java
@PersistenceContext
private EntityManager entityManager;

@Transactional
public void processUser(long id) {
    var userEntity = userRepository.findById(id).orElseThrow();
    System.out.println("User name: " + userEntity.getName());

    userPersistenceService.renameUser(id, "Alice in Wonderland");

    entityManager.clear(); // ❗ Clears persistence context, forcing fresh DB query
    var renamedUser = userRepository.findById(id).orElseThrow();
    System.out.println("Renamed name: " + renamedUser.getName());
}
```

---

#### 2️⃣ **Use `entityManager.refresh()` Instead of Clearing**
Instead of clearing the whole context, refresh just the entity:
```java
@PersistenceContext
private EntityManager entityManager;

@Transactional
public void processUser(long id) {
    var userEntity = userRepository.findById(id).orElseThrow();
    System.out.println("User name: " + userEntity.getName());

    userPersistenceService.renameUser(id, "Alice in Wonderland");

    entityManager.refresh(userEntity); // ❗ Forces a fresh DB read for this entity only
    System.out.println("Renamed name: " + userEntity.getName());
}
```
This is **more efficient** because it updates only the specific entity instead of clearing the entire persistence context.

---

#### 3️⃣ **Disable Persistence Context Caching**
Another approach is setting `@Transactional` with `propagation = Propagation.NOT_SUPPORTED` on `processUser()`, which **disables the transactional context**:
```java
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public void processUser(long id) { ... }
```
However, this removes transactional benefits for `processUser()`, which is usually not desirable.

---

### 🚀 **Final Takeaway**
- **`REQUIRES_NEW` starts a new transaction**, so changes are committed but are **not reflected** in the outer transaction’s persistence context.
- **Hibernate caches entities per transaction**, preventing the renamed entity from being visible in `processUser()`.
- **Use `entityManager.clear()` or `entityManager.refresh()`** to force a fresh database read.

Would you like further tweaks based on your specific use case? 🚀