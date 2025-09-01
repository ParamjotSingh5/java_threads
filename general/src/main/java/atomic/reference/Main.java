package atomic.reference;

import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        String oldName = "Bruce Wayne";
        String newName = "Batman";
        AtomicReference<String > atomicReference = new AtomicReference<>(oldName);

        atomicReference.set(newName);
        if(atomicReference.compareAndSet(oldName, newName)) {
            System.out.println("Name updated to: " + atomicReference.get());
        } else {
            System.out.println("Update failed. Current name: " + atomicReference.get());
        }

    }
}
