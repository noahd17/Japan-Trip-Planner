package com.noah.japantripplanner;

/**
 * A single item on the packing list, stored as a document in the
 * Firestore collection: users/{uid}/packingItems/{itemId}
 */
public class PackingItem {

    private String id;      // Firestore document ID, set after read
    private String name;
    private boolean packed;

    // Firestore requires a no-argument constructor for deserialization
    public PackingItem() {
    }

    public PackingItem(String name, boolean packed) {
        this.name = name;
        this.packed = packed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPacked() {
        return packed;
    }

    public void setPacked(boolean packed) {
        this.packed = packed;
    }
}
