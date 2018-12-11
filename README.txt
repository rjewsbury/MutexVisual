--------------------------------------------
Mutex Algorithm Viewer - User Guide
--------------------------------------------

Table of Contents:

1. The Editor
    a. Files
    b. Keywords
        i.  Shared
        ii. N
        iii.ID 
    c. Key Methods
        i. void initializeShared()
        ii. void initialize()
        iii. void algorithm()
        iv. CriticalSection()
    d. Linked Variables
    e. Compiling
    
2. The Simulator
    a. Displays
        i. The Code Display
        ii. The Variable Display
        iii. The Algorithm display
    b. Controls
        i. Active Threads
        ii. Speed Control
        iii. Execution Control 
    
--------------------------------------------
2. The Editor
    The Algorithm Editor allows users to write a mutual exclusion algorithm
    using Java code, and link the code to a pre-built display through
    shared variables.
    
    For examples of using the editor, see the "Help" tab in the menu bar

    a. Files
        Previously created algorithms can be loaded through the menu bar
        under ( File > Open File... ) and navigating to a .mutex file.
        Several algorithms have been included with the simulator in the
        Algorithms folder.
        If a configuration is created or changed, it can be saved to a
        .mutex file for others to use.

    b. Keywords
        See "Example (Keywords)" under the help menu for examples
        
        i. Shared:
            Global variables can belong to a single thread, or be accessed as
            shared memory by an entire group of threads. These shared variables
            are created using the "shared" keyword, similarly to "static"
            
            Shared variables also change the flow of execution. Whenever a
            thread is about to read or write a shared variable, it will pause,
            to more clearly demonstrate any race conditions
            
        ii. N:
            N is a special variable that stores the current number of threads
            in the simulation. N can be changed in the editor under the
            "Settings" tab
            
        iii. ID:
            ID is a special variable that stores the current thread's ID.
            Thread IDs are numbers from 0 to N-1 inclusive
            
    c. Key Methods
        See "Example (Key methods)" under the help menu for examples
        i. void initializeShared():
            This method is run once before execution.
            It is meant for initializations of shared variables that are
            too complicated to fit on a single line.
            This is not required
            
        ii. void initialize():
            This method is run once by each thread before execution.
            Similar to initializeShared, but meant for
            individual-thread variables
            This is not required
            
        iii. void algorithm():
            This method is called repeatedly in a loop by each thread.
            The main execution of the mutual exclusion algorithm is written here
            This IS required
            
        iv. CriticalSection():
            This method MUST be called within the algorithm method.
            When a thread executes this method, the display places its
            representation in the CriticalSection display area
    
    d. Linked Variables
        See "Example (Linked Variables)" under the help menu for examples
        Under the "Settings" tab there is a drop down menu to select
        a display type. Each display type requires certain types of
        variables in order to monitor the state of the algorithm.
        
        The names of the currently linked variables can be seen under
        the "Display" tab, and can be changed to any global variable that
        has the correct type within the Algorithm class in the "Algorithm" tab.
        Both shared and non-shared global variables can be linked.
        
    e. Compiling
        Once an algorithm has been loaded or written, and the display has been
        linked, the code can be compiled by clicking the "Compile & Run" button.
        If the code is not able to compile, Any compiler errors will be
        displayed for the user. If the code compiles correctly, the Simulator
        will be loaded in a new window with the compiled algorithm
        (If the editor is being run on JRE 9 or 10, The editor will not have
        access to a compiler, and will alert the user.)

--------------------------------------------
2. The Simulator
    a. Displays
        The Simulator has four main display windows...
        i. The Code Display:
            This display shows the algorithm code as written in the editor.
            As threads execute the code, A box labelled with their ID
            will move through the code, pausing whenever a shared variable
            is read or written to show the next operation.
            
        ii. The Thread Display:
            This display shows the progress of each thread as it moves through
            the algorithm. It is a more abstract representation of progress,
            which makes it easier to tell at a glance where each thread is.
            This display can be disabled in the view menu.
            
        iii. The Variable Display:
            This display shows the contents of memory used by the algorithm.
            Whenever a thread accesses or manipulates a variable, a box
            labelled with their ID will appear over that variable.
            Shared variables are shown by name, while non-shared variables
            are shown with the prefix "Thd#:" to display which thread they
            belong to. This display can be disabled in the view menu
            
        iv. The Algorithm display:
            This display shows an abstract representation of how the algorithm
            is behaving. As the linked variables change, the display updates
            to represent the current state
    b. Controls
        i. Active Threads:
            Each thread can be activated or deactivated seperately, in order
            to simulate high contention or low contention situations.
            At the top of the control panel, there is a row of check boxes.
            When a box labelled with an ID is checked, the thread with that
            ID is eligble to continue execution.
            When the box is unchecked, that thread will be paused in its current
            state. This can be used to either have threads not participate,
            or to cause threads to stay at certain key checkpoints in the
            algorithm for an indefinite period.
            
        ii. Speed Control:
            The algorithm is run by choosing a thread from the list of active
            threads, and having it execute a piece of code up to the next
            shared memory access. This allows the algorithm to be run at
            a reasonable pace to visualize, while not sacrificing any of the
            possible behaviours that may occur when the code is run at full
            speed.
            The slider in the control menu allows the user to change the
            speed of the algorithm by changing the waiting period between
            steps of execution.
            
        iii. Execution Control:
            Run / Stop:
                Run causes the algorithm to begin execution at the speed
                given by the Speed Controls. All active threads will begin
                moving through the algorithm. Stop causes execution to freeze
                in whatever state it currently has.
            Step:
                This causes a random active thread to move one step to the next
                shared memory access. By carefully manipulating active threads
                and using the step function, Edge cases can be created and tested
            Reset:
                This causes all threads to return to their initial state at
                the beginning of the algorithm, resetting both their positions
                and the state of all variables.
                The field next to the reset button determines the number of
                threads to be used by the algorithm upon reset. Any integer
                greater than 1 may be used, however larger numbers will suffer
                on performance.
                
            When the simulation is done being used, or the user wishes to
            change the number of threads, the simulation window can simply
            be closed, the change can be made in the editor, and the algorithm
            can be re-compiled.
