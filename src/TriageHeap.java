import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriageHeap {

    private final List<Patient> heap;
    private final Map<Integer, Integer> positionMap; // patientId → heap index (for O(log n) update)

    public TriageHeap() {
        this.heap = new ArrayList<>();
        this.positionMap = new HashMap<>();
    }

    // ── Core heap operations ──────────────────────────────────────────────

    public void insert(Patient p) {
        heap.add(p);
        int idx = heap.size() - 1;
        positionMap.put(p.getId(), idx);
        siftUp(idx);
    }

    public Patient peek() {
        if (isEmpty()) throw new IllegalStateException("Triage queue is empty");
        return heap.get(0);
    }

    public Patient poll() {
        if (isEmpty()) throw new IllegalStateException("Triage queue is empty");
        Patient top = heap.get(0);
        positionMap.remove(top.getId());

        int last = heap.size() - 1;
        swap(0, last);
        heap.remove(last);

        if (!isEmpty()) siftDown(0);
        return top;
    }

    // Called by aging thread — O(log n) per patient
    public void updateScore(Patient p) {
        Integer idx = positionMap.get(p.getId());
        if (idx == null) return;
        // After score increased, patient can only move UP
        siftUp(idx);
    }

    // Called to recalculate all scores (e.g. on aging tick)
    public void reheapifyAll() {
        for (Patient p : heap) p.calculateScore();
        // Floyd's algorithm: O(n) full rebuild
        for (int i = heap.size() / 2 - 1; i >= 0; i--) siftDown(i);
        // Rebuild positionMap
        positionMap.clear();
        for (int i = 0; i < heap.size(); i++) positionMap.put(heap.get(i).getId(), i);
    }

    public boolean isEmpty()  { return heap.isEmpty(); }
    public int size()         { return heap.size(); }

    // ── Internal sift operations ──────────────────────────────────────────

    private void siftUp(int idx) {
        while (idx > 0) {
            int parent = (idx - 1) / 2;
            if (heap.get(idx).compareTo(heap.get(parent)) >= 0) break; // parent is higher priority
            swap(idx, parent);
            idx = parent;
        }
    }

    private void siftDown(int idx) {
        int size = heap.size();
        while (true) {
            int left  = 2 * idx + 1;
            int right = 2 * idx + 2;
            int largest = idx;

            if (left  < size && heap.get(left).compareTo(heap.get(largest))  < 0) largest = left;
            if (right < size && heap.get(right).compareTo(heap.get(largest)) < 0) largest = right;

            if (largest == idx) break;
            swap(idx, largest);
            idx = largest;
        }
    }

    private void swap(int i, int j) {
        Patient pi = heap.get(i), pj = heap.get(j);
        heap.set(i, pj);
        heap.set(j, pi);
        positionMap.put(pi.getId(), j);
        positionMap.put(pj.getId(), i);
    }

    // ── Display ───────────────────────────────────────────────────────────

    public void printQueue() {
        if (isEmpty()) { System.out.println("  (queue is empty)"); return; }
        System.out.println("  ┌─ Triage Queue (highest priority first) ─────────────────┐");
        // Sort a copy for display — don't disturb the heap
        heap.stream()
        .sorted()
        .forEach(p -> System.out.println("  │  " + p));
        System.out.println("  └──────────────────────────────────────────────────────────┘");
    }
}