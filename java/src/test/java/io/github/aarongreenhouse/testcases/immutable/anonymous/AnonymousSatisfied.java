package io.github.aarongreenhouse.testcases.immutable.anonymous;

import io.github.aarongreenhouse.promises.Immutable;

interface Listener {
    void onEvent();
}

class Task {
    void run() {}
}

class Dispatcher {
    // IMM-ANONYMOUS not applicable: the interface Listener is not immutable
    final Listener listener = new Listener() {
        @Override
        public void onEvent() {}
    };

    // IMM-ANONYMOUS not applicable: the class Task is not immutable
    final Task task = new Task() {
        @Override
        void run() {}
    };

    // IMM-ANONYMOUS not applicable: the class java.lang.Object is not immutable
    final Object lock = new Object() {};
}

@Immutable
class Scheduler {
    void schedule() {
        // IMM-ANONYMOUS not applicable: the class Task is not immutable. An anonymous
        // class may appear in the body of an immutable class.
        Task task = new Task() {};
    }
}
