# Mutex Algorithm Viewer
This project is a visualization and experimentation tool for concurrent algorithms.

## Setup
This project requires JDK 8 to run since `javac` is used within it. Most IDEs only import the JRE, and so you may experience issue compiling the program. You will need to link `tools.jar` into the project, which is located in the JDK 8 folder (`Drive:/Program Files/jdk1.8.x_z/lib/`).

## User Guide

### Table of Contents

1. #### The Editor

	1. ##### Files
    2. ##### Keywords

		1. ###### `shared`
		2. ###### `N`
		3. ###### `ID`

    3. ##### Key Methods

		1. ###### `void initializeShared()`
		2. ###### `void initialize()`
		3. ###### `void algorithm()`
		4. ###### `CriticalSection()`

    4. ##### Linked Variables
    5. ##### Compiling

2. #### The Simulator

	1. ##### Displays

		1. ###### Code Display
		2. ###### Variable Display
		3. ###### Algorithm Display

	2. ##### Controls

		1. ###### Active Threads
		2. ###### Speed Control
		3. ###### Execution Control

---

#### 1. The Editor
The Algorithm Editor allows users to write a mutual exclusion algorithm using Java code, and link the code to a pre-built display through shared variables.

For examples of using the editor, see the `Help` tab in the menu bar.

##### i. Files
Previously created algorithms can be loaded through the menu bar under `(File > Open File... )` and navigating to a .mutex file. Several algorithms have been included with the simulator in the Algorithms folder. 
If a configuraion is created or changed, it can be saved to a **.mutex** file for others to use.

##### ii. Keywords
See `Example (Keywords)` under the help menu for examples.

###### a. `shared`
Global variables can belong to a single thread, or be accessed as shared memory by an entire group of threads. These shared variables are created using the `shared` keyword, similarly to `static`.

Shared variables also change the flow of execution: whenever a thread is about to read or write a shared variable, it will pause, to more clearly demonstrate any race conditions.

###### b. `N`
`N` is a special variable that store the current number of threads in the simulation. `N` can be changed in the editor under the `Settings` tab.

###### c. ID
`ID` is a special variable that stores the current thread's ID. Thread IDs are numbers from *0* to *N-1* inclusive.

##### iii. Key Methods
See `Example (Key Methods)` under the help menu for examples.

###### a. `void initializeShared()`
This method is optional and ran once before execution. It is meant for initializations of shared variables that are too complicated to fit on a single line.

###### b. `void initialize()`
This method is optional and ran once by each thread before execution. Similar to `initializeShared`, but meant for individual-thread variables.

###### c. `void algorithm()`
This method is required and called repeatedly in a loop by each thread. The main execution of the mutual exclusion algorithm is written here.

###### d. `CriticalSection()`
This method *must* be called within the algorithm method. When a thread executes this method, the display places its representation in the `CriticalSection` display area.

##### iv. Linked Variables
See `Example (Linked Variables)` under the help menu for examples. Under the `Settings` tab there is a drop down menu to select a display type. Each display type requires certain types of variables in order to monitor the state of the algorithm.

The names of the currently linked variables can be seen under the `Display` tab, and can be changed to any global variable that has the correct type within the `Algorithm` class in the `Algorithms` tab. Both shared and non-shared global variables can be linked.

##### v. Compiling
Once an algorithm has been loaded or written, and the display has been linked, the code can be compiled by clicking the `Compile & Run` button. If the code is not able to compile, any compiler errors will be displayed for the user. If the code compiles correctly, the **Simulator** will be loaded in a new window with the compiled algorithm.

**Note:** If the editor is being run on JRE 9 or 10, the editor will not have access to a compiler and will alert the user.

---

#### 2. The Simulator
##### i. Displays
The simulator has four main display windows:
###### a. Code Display
This display shows the algorithm code as written in the editor. As threads execute the code, A box labeled with their ID will move through the code, pausing whenever a shared variable is read or written to show the next operation. 
###### b. Thread Display
This display shows the progress of each thread as it moves through the algorithm. It is a more abstract representation of progress, which makes it easier to tell at a glance where each thread is. This display can be disabled in the `View` menu.
###### c. Variable Display
This display shows the contents of memory used by the algorithm. Whenever a thread accesses or manipulates a variable, a box labeled with their ID will appear over that variable. Shared variable are shown by name, while non-shared variables are shown with the prefix `Thd#:` to display which thread they belong to. This display can be disable in the `View` menu.
###### d. Algorithm Display
This display shows an abstract representation of how the algorithm is behaving. As the linked variables change, the display update to represent the current state.

##### ii. Controls
###### a. Active Threads
Each thread can be activated or deactivated separately in order to simulate high contention or low contention situations. At the top of the control panel, there is a row of check boxes. When a box labeled with an ID is checked, the thread with that ID is eligible to continue execution.
When the box is unchecked, that thread will be paused in its current state. This can be used to either have threads not participate or to cause threads to stay at certain key checkpoints in the algorithm for an indefinite period.

###### b. Speed Control
The algorithm is run by choosing a thread from the list of active threads and having it execute a piece of code up to the next shared memory access. This allows the algorithm to be run at a reasonable pace to visualize while not sacrificing any of the possible behaviours that may occur when the code is run at full speed. The slider in the control menu allows the user to change the speed of the algorithm by changing the waiting period between steps of execution.

###### c. Execution Control
* **Run/Stop**: Run causes the algorithm to begin execution at the speed given by the speed controls. All active threads will begin moving through the algorithm. Stop causes execution to freeze in whatever state it currently is in.
* **Step**: This causes a random active thread to move one step to the next shred memory access. By carefully manipulating active threads and using the step function, edge cases can be created and tested.
* **Reset**: This causes all threads to return to their initial state at the beginning of the algorithm, resetting both their positions and he state of all variables. The field next to the `Reset` button determines the number of threads to be used by the algorithm upon reset. Any integer greater than 1 may be used, however large numbers will hinder performance.

When the simulation is done being used or the user wishes to change the number of threads, the `Simulation` window can simply be closed, the change can be made in the editor, and the algorithm can be re-compiled.
