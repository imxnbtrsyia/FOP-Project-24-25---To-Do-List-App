package recurringtask;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static recurringtask.RecurringTask.actionInDBR;
import static recurringtask.RecurringTask.taskList;
import static recurringtask.allparts.actionInDB;
import static recurringtask.allparts.deleteTaskArr;
import static recurringtask.allparts.i;
import static recurringtask.allparts.loadDBR;
import static recurringtask.allparts.numTask;
import static recurringtask.allparts.taskCompletion;

//this class is to used in method to detect cycle of dependencies
class ListNode {
    int val;
    ListNode next;

    // Constructor to create a new node
    ListNode(int val) {
        this.val = val;
        this.next = null;
    }
}

public class TunaMayo { //make sure betulkan jugak code dekat line 350 ikut nama file korg save.
    public static String recurrenceInterval; //"weekly", "daily", "monthly"
    public static LocalDate nextOccurence;
    public static LocalDate due;
    public static java.sql.Date sqlDue;
    public static java.sql.Date sqlnextOcc;
    //public static ArrayList<Task> taskList = new ArrayList<>();
    public static int i = 1;
    public static int complete;
    public static int numTask;
    
    public static void main(String[] args) throws Exception{
        Scanner input = new Scanner(System.in);
        int i = 0;
        boolean taskEXIST = false;
        int i_depend = 0;
        int count_Depend = 0;
        int last_num=0;
        int actforDB = 0;
        String recepient= "";
       
        //Creating arraylist to store task information
        ArrayList<Integer> taskNumber = new ArrayList<>();
        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> describe = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();
        ArrayList<String> category = new ArrayList<>();
        ArrayList<String> priority = new ArrayList<>();
        ArrayList<Integer> status = new ArrayList<>();
        ArrayList<Integer> dependency = new ArrayList<>();
        int [] justEXISTEDtask = new int[2];
        
        //basically this method nak fetch balik data2 yg dah penah existed dlm db
        justEXISTEDtask = forNumberingTask(justEXISTEDtask, taskEXIST, i, last_num, taskNumber, title, describe, date, category, priority, status, dependency, i_depend);
        recepient = EmailAddressfromDB();
        loadDBR();
        RecurringTask.scheduleRecurringTaskCheck();
        
        i = justEXISTEDtask[0]; 
        i_depend = justEXISTEDtask[1];
        int i_old = i;
        int inc = 0;
              
        //Variables needed for coding of task dependencies
            int [] taskPreceding = new int[i_depend];
            int [] dependent = new int[i_depend];

            // Create an array of ListNode to store the nodes
            ListNode[] nodes = new ListNode[0];
            
            if (i!=0){
                for (int k=0, j=2; k<i_depend;k++,j+=2){
                   dependent[k]=justEXISTEDtask[j]; 
                   taskPreceding[k]=justEXISTEDtask[j+1];
                }
                nodes = Arrays.copyOf(nodes, nodes.length + i); //updated array nodes

                // Input: Create nodes for each value
                for (int j = 0; j<i; j++) {
                   nodes[j] = new ListNode(j);
                }
            }
        
        //User select which procedure
        while (true) {
            if (recepient!="0"){ dueNoti(title, date, recepient); }
            System.out.print("What do you want to do today ");
            System.out.println("\n1) Create a task");
            System.out.println("2) Add a recurring task");
            System.out.println("3) View all tasks");
            System.out.println("4) Manage task");
            System.out.println("5) Delete task");
            System.out.println("6) Sort Task");
            System.out.println("7) Search Task");
            System.out.println("8) Edit task");
            System.out.println("9) Your tasks analytics");
            System.out.println("10) Exit program");
            System.out.print("Enter number: ");
            int option = input.nextInt();
            input.nextLine();
        
            switch (option){
                case 1:
                    i = createTask(i, taskNumber, title, describe, date, category, priority, status, dependency, input);
                    int countNewTask = i - i_old; //ni untuk contohnye, sblm ni, user dh penah masuk 3 tasks, so lps tu dia run java again, so dptlah brp byk newTask yg dia create lps tu
                                   
                    // array of ListNode to store the nodes
                    nodes = Arrays.copyOf(nodes, nodes.length + countNewTask);
                    for (int j = i_old; j<countNewTask; j++) {
                        nodes[j] = new ListNode(j);
                    }
                    i_old = i; 
                    break;
                case 2:
                    inc = RecurringTask.getNumTaskDB();
                    RecurringTask.addRecurringTask(inc);
                    break;
                case 3:  
                    System.out.println("");
                    actionInDB(4,0, null, null, null, null, null,0,0,null,null,0,0, null);
                    System.out.println("=== Recurring Tasks ===");
                    RecurringTask.actionInDBR(5, 0, null, null, null, 0);
                    System.out.println("\n\n");
                    break;
                case 4:
                    System.out.println("Which type of task do you want to mark complete?\n1. Regular Task\n2. Recurring Task");
                    int type = input.nextInt();
                    //arrays needed to check existance of task dependencies in taskCompletion() method
                    taskPreceding = Arrays.copyOf(taskPreceding, taskPreceding.length);
                    dependent = Arrays.copyOf(dependent, dependent.length);
                        
                    boolean depend = false;
                    if (type == 1) {
                        taskCompletion(status, taskNumber, title, dependent, taskPreceding, depend, count_Depend, input);
                        System.out.println("");
                    } else if (type == 2) {
                        System.out.println("\n=== Mark Task as Complete ===");
                        RecurringTask.actionInDBR(3, 0, null, null, null, 0);
                        if (taskList.isEmpty()){
                            System.out.println("No tasks available.");
                        break;
                    }
                        System.out.println("");
                    System.out.print("Which task do you want to mark as completed? (Insert number): \n");
                    int numTask = input.nextInt();
                    //check dulu task complete = 1 dgn numTask = 1 tu sama ke..
                    boolean taskAlrComp = RecurringTask.taskAlreadyComplete(numTask-1);
                    if (taskAlrComp){
                        System.out.println("Task is already completed.");
                        System.out.println("");
                        break;
                    } else
                        RecurringTask.completeTask(numTask-1);
                    }
                    break;
                case 5:
                    System.out.println("");
                    System.out.println("========== Delete Task =========="); 
                    
                    System.out.println("Which type of task do you want to delete?\n1. Regular Task\n2. Recurring Task");
                    int type2 = input.nextInt();
                    if (type2 == 1){
                    deleteTaskArr(taskNumber, title, describe, date, category, priority, status, dependency, input);
                    } else if (type2 == 2) {
                        actionInDBR(3, 0, null, null, null, 0);
                        System.out.println("");
                        System.out.print("Which task do you want to delete? (Insert number) : ");
                        int taskId = input.nextInt();
                        input.nextLine();

                        if (taskId-1 < 0 || taskId-1 >= taskList.size()){
                            System.out.println("Invalid task ID.\n");
                        } else {
                            RecurringTask.deleteTask(taskId-1);
                        }
                    }
                    break;
                case 6:
                    sortTask(taskNumber, title, describe, date, category, priority, status,input);
                    break;
                case 7:
                    searchTask(title, describe, date, category, priority, taskNumber, status, dependency, input);
                    break;
                case 8:
                    System.out.println("\n=== Edit Task ===");
                    System.out.println("Which type of task do you want to delete?\n1. Regular Task\n2. Recurring Task");
                    int type3 = input.nextInt();
                    
                    if (type3 == 1){
                        System.out.print("Enter the task number you want to edit: ");
                       int taskToEdit = input.nextInt();
                       System.out.print("\n\nWhat would you like to edit? \n1. Title\n2. Description\n3. Due Date\n4. Category\n5. Priority\n6. Set Task Dependency \n7. Cancel \nEnter no.: ");
                       int edit = input.nextInt();
                       while (edit!=7){
                           switch (edit){
                               case 1:
                                   System.out.print("Enter the new title: ");
                                   input.nextLine();
                                   String newTitle = input.nextLine();
                                   title.set(taskToEdit-1, newTitle);
                                   System.out.println("New title is: "+ title.get(taskToEdit-1));
                                   actionInDB(8,taskToEdit,newTitle,null ,null,null,null,0,0,null,null,0,0,null); //update completion in database
                                   break;
                               case 2:
                                   System.out.print("Enter the new description: ");
                                   input.nextLine();
                                   String newDescription = input.nextLine();
                                   describe.set(taskToEdit-1, newDescription);
                                   actionInDB(9,taskToEdit,null,newDescription,null,null,null,0,0,null,null,0,0,null); //update completion in database
                                   break;
                               case 3:
                                   System.out.print("Enter the new due date (YYYY-MM-DD): ");
                                   input.nextLine();
                                   String newDue = input.nextLine();
                                   date.set(taskToEdit-1, newDue);
                                   actionInDB(10,taskToEdit,null,null,newDue,null,null,0,0,null,null,0,0,null); //update completion in database
                                   break;
                               case 4:
                                   System.out.print("Enter the new category (Homework / Personal / Work) : ");
                                   input.nextLine();
                                   String newCategory = input.nextLine();
                                   category.set(taskToEdit-1, newCategory);
                                   actionInDB(11,taskToEdit,null,null,null,newCategory,null,0,0,null,null,0,0,null);
                                   break;
                               case 5:
                                   System.out.println("Enter the new priority level (High, Medium, Low)");
                                   input.nextLine();
                                   String newPriority = input.nextLine();
                                   priority.set(taskToEdit-1, newPriority);
                                   actionInDB(12,taskToEdit,null,null ,null,null,newPriority,0,0,null,null,0,0,null);
                                   break;
                               case 6:
                                   //Variables needed for coding of task dependencies
                                   taskPreceding = Arrays.copyOf(taskPreceding, taskPreceding.length + 1);
                                   dependent = Arrays.copyOf(dependent, dependent.length + 1);
                                   nodes = Arrays.copyOf(nodes, nodes.length);

                                   taskDependency(i_depend, nodes, dependent, taskPreceding, title, input, taskToEdit);
                                   i_depend++;
                                   break;
                               case 7:
                                   break;
                               default:
                                   System.out.println("Enter valid input");
                                   break;
                                }
                            System.out.print("\nWhat would you like to edit? \n1. Title\n2. Description\n3. Due Date\n4. Category\n5. Priority\n6. Set Task Dependency \n7. Cancel \nEnter no.: ");
                            edit = input.nextInt();
                                }
}
                    else if (type3 == 2){
                       
                        RecurringTask.actionInDBR(3, 0, null, null, null, 0);
                        System.out.println("");
                       System.out.print("Which task do you want to edit? (Insert number): ");
                        int taskToEditR = input.nextInt();
                        input.nextLine();
                        if (taskToEditR-1 < 0 || taskToEditR-1 >= taskList.size()){
                            System.out.println("Invalid task ID.\n");
                        } else {
                            Task task = taskList.get(taskToEditR-1);
                            RecurringTask recurringTask = (RecurringTask) task;
                            System.out.print("\n\nWhat would you like to edit? \n1. Title\n2. Description\n3. Recurrence Interval\n4. Completion\n5. Cancel\nEnter no.: ");
                            int edit1 = input.nextInt();
                        
                         
                            while (edit1!=5){
                                switch (edit1){
                                    case 1:
                                        System.out.print("Enter the new title: ");
                                        input.nextLine();
                                        String newTitle = input.nextLine();
                                        task.updateTitle(newTitle);
                                        System.out.println("Task '"+ RecurringTask.getTitleDB(taskToEditR-1) +"' has been updated to '"+newTitle+"'!");
                                        RecurringTask.actionInDBR(9, taskToEditR-1, newTitle, null, null, 0);
            
                                        break;
                                    case 2:
                                        System.out.print("Enter the new description: ");
                                        input.nextLine();
                                        String newDescription = input.nextLine();
                                        task.updateDescription(newDescription);
                                        System.out.println("Task '"+ RecurringTask.getTitleDB(taskToEditR-1) +"' has been updated to a new description '"+newDescription+"'!");
                                        RecurringTask.actionInDBR(10, taskToEditR-1, null, newDescription, null, 0);
                                        break;
                                    case 3:
                                        System.out.print("Enter the new recurrence interval (Daily / Weekly / Monthly) : ");
                                        input.nextLine();
                                        String newRecurrenceInterval = input.nextLine();
                                        recurringTask.updateRecurrenceInterval(newRecurrenceInterval); //update recurrence interval, next occurence, due date
                                        System.out.println("Task '"+ RecurringTask.getTitleDB(taskToEditR-1) +"' has been updated to a new recurrence interval '"+newRecurrenceInterval+"'!");
                                        RecurringTask.actionInDBR(11, taskToEditR-1, null, null, newRecurrenceInterval, 0);
                                        RecurringTask.actionInDBR(13, taskToEditR-1, null, null, null, 0);
                                        break;
                                    case 4:
                                        System.out.print("This is to mark the task incomplete. Proceed? (Yes / No) : ");
                                        input.nextLine();
                                        String ans = input.nextLine();
                                        if (ans.equalsIgnoreCase("yes")){
                                            recurringTask.updateCompletion(0);
                                            System.out.println("Task '"+ RecurringTask.getTitleDB(taskToEditR-1) +"' has been updated incomplete!");
                                            RecurringTask.actionInDBR(7, taskToEditR-1, null, null, null, 0);
                                            RecurringTask.actionInDBR(13, taskToEditR-1, null, null, null, 0);
                                        } else {
                                            break;
                                        }
                                        break;
                                    case 5:
                                        break;
                                    default:
                                        System.out.println("Enter valid input");
                                        break;
                                }
                    System.out.print("\nWhat would you like to edit? \n1. Title\n2. Description\n3. Recurrence Interval\n4. Completion\n5. Cancel \nEnter no.: ");
                    edit1 = input.nextInt();
                    }
                  }
                }
                    System.out.println("");

                    break;
                case 9:
                    dataAnalytics(i,taskNumber,status,category);
                    System.out.println("\n");
                    break;
                case 10:
                    System.out.println("Before exit, would you like to receive email notification from us? \nWe will help remind you when the due date for your tasks is approaching.");
                    System.out.print("(Enter \"No\" if you have already entered your email address) [Yes/No]: ");
                    if (input.nextLine().equalsIgnoreCase("yes")){
                        System.out.print("\nEnter your email address: ");
                        recepient = input.nextLine();
                        actionInDB(7,0, null, null, null, null, null,0,0,null,null,0,0,recepient);
                        System.out.println("Thank you! We will notify you when a task is due within 24 hours.");
                    }
                    System.out.println("Bye for Now!");
                    return;
                default:
                    System.out.println("Invalid number please enter again");
                    System.out.println("");
                    break;    
            }
        }
    }
    
    
    //basically this method nak fetch balik data2 yg dah ade dlm db, so then bile user stop run java for 1st time, then the 2nd time dia run java, user still ade data yg dia dh masukkan tdi
    public static int [] forNumberingTask(int [] justEXISTEDtask, boolean taskEXIST, int i, int last_num, ArrayList<Integer> taskNumber, ArrayList<String> title, ArrayList<String> describe, ArrayList<String> date, ArrayList<String> category, ArrayList<String> priority, ArrayList<Integer> status, ArrayList<Integer> dependency, int i_depend) {
        Connection conn1 = null; 
        try {
        String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
        String user = "root";
        String password = "";

        //connect with your database
        conn1 = DriverManager.getConnection(url1, user, password);
        if (conn1 != null) {
            int taskEXISTEDinDB = 0;
            int for_i_depend = 2;

            //NAK CARIK APE INDEX TASK LAST DALAM DB, THEN BOLEH FETCH DATA DRI DB MASUK SINI
            String sql4 = "SELECT * FROM task order by numTask desc limit 1";
            var carik = conn1.prepareStatement(sql4);

            var last = carik.executeQuery();
            while (last.next()){
                taskEXIST = true; //if true, maksudnye before this, dah ade task pernah dimasukkan dlm db
                last_num = last.getInt(1);
                i = last_num+1;

                String sql5 = "SELECT * FROM task order by numTask asc";
                var isiArr = conn1.prepareStatement(sql5);

                var fill = isiArr.executeQuery(); //maksud isiArr ni is nak isi array dgn task2 yg dh pernah added dlm db, so that kat main() nnti user boleh keep smbung dia punye to do list even if java is distop runnye byk kali
                while (fill.next()){
                    justEXISTEDtask = Arrays.copyOf(justEXISTEDtask, justEXISTEDtask.length + (i_depend+1));
                    taskNumber.add((fill.getInt(1)+1));
                    title.add(fill.getString(2));
                    describe.add(fill.getString(3));
                    date.add(fill.getString(4));
                    category.add(fill.getString(5));
                    priority.add(fill.getString(6));
                    status.add(fill.getInt(7));
                    dependency.add(fill.getInt(8));
                    
                    if (dependency.get(taskEXISTEDinDB)!=-1){
                        justEXISTEDtask = Arrays.copyOf(justEXISTEDtask, justEXISTEDtask.length + (for_i_depend+2));
                        justEXISTEDtask[for_i_depend]= taskNumber.get(taskEXISTEDinDB)-1;
                        justEXISTEDtask[for_i_depend+1]= dependency.get(taskEXISTEDinDB);
                        for_i_depend+=2;
                        i_depend++;
                        
                    }
                    taskEXISTEDinDB++;
                }
            } 
            if (taskEXIST==false) {
                i = 0;
                i_depend = 0;
            } 
            justEXISTEDtask[0] = i;
            justEXISTEDtask[1] = i_depend;
        }
        }catch(SQLException ex){
            System.out.println("An error occurred");
            ex.printStackTrace();
        }
        finally {
            if (conn1 != null) {
                try {
                    conn1.close();
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return justEXISTEDtask;
    }
        
    public static void loadDBR(){
        Connection conn1 = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn1 = DriverManager.getConnection(url1, user, password);
            if (conn1 != null) {
                String sql = "SELECT numTask, Title, Description, RecurrenceInterval, NextOccurence, Due, Completion FROM recurtask"; // Adjust table/column names

                PreparedStatement stmt = conn1.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery(); 
                
                int currentNumTask = RecurringTask.getNumTaskDB();
                while (rs.next()) {
                    numTask = rs.getInt("numTask");
                    String title1 = rs.getString("title");
                    String description1 = rs.getString("description");
                    String recurrence = rs.getString("RecurrenceInterval");
                    java.sql.Date nextocc = rs.getDate("NextOccurence");
                    java.sql.Date duedate = rs.getDate("Due");
                    int isCompleted = rs.getInt("completion");

                    LocalDate nextocc2 = nextocc.toLocalDate();
                    LocalDate duedate2 = duedate.toLocalDate();


                    RecurringTask task = new RecurringTask(currentNumTask, title1, description1, recurrence, nextocc2, duedate2, isCompleted); // Assuming Task has this constructor
                    taskList.add(task); 
                    i = Math.max(i, numTask + 1);
                    currentNumTask++;
                        }
            }
        }catch(SQLException ex){
            System.out.println("An error occurred");
            ex.printStackTrace();
        }
        finally {
            if (conn1 != null) {
                try {
                    conn1.close();
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static int createTask(int i, ArrayList<Integer> taskNumber, ArrayList<String> title, ArrayList<String> describe, ArrayList<String> date, ArrayList<String> category, ArrayList<String> priority, ArrayList<Integer> status, ArrayList<Integer> dependency, Scanner input){
        while (true) {
            System.out.println("");
            System.out.println("========== Add a New task ==========");
            System.out.print("Enter task title: ");
            title.add(input.nextLine());
            System.out.print("Enter task description: ");
            describe.add(input.nextLine());
            System.out.print("Enter due date (YYYY-MM-DD): ");
            date.add(input.nextLine());
            System.out.print("Enter task category (Homework / Personal / Work): ");
            category.add(input.nextLine());
            System.out.print("Enter priority level (High / Medium / Low): ");
            priority.add(input.nextLine());   
            
            taskNumber.add((i+1));
            String title1 = title.get(i);
            String describe1 = describe.get(i);
            String  date1 = date.get(i);
            String category1 = category.get(i);
            String priority1 = priority.get(i);
            status.add(0);  
            dependency.add(null);
            
            actionInDB(1,i, title1, describe1, date1, category1, priority1,0,0,null,null,0,0,null);
            System.out.println("Task \"" + title.get(i) + "\" added succesfully!");
            i++; 
            System.out.print("\nAdd more task? (Enter Yes / No): ");
            String choice = input.nextLine();
            if (!choice.equalsIgnoreCase("yes")) {
                System.out.println("");
                return i;
            }
        }
    }
    
    public static void deleteTaskArr(ArrayList<Integer> taskNumber, ArrayList<String> title, ArrayList<String> describe, ArrayList<String> date, ArrayList<String> category, ArrayList<String> priority, ArrayList<Integer> status, ArrayList<Integer> dependency, Scanner input){
        while (true){
            System.out.println("");
            System.out.println("========== Delete Task =========="); 
            System.out.print("Enter task number: ");
            int taskDEL = input.nextInt()- 1;
            input.nextLine();
            
            actionInDB(2,0,null,null ,null,null,null,taskDEL,0,null,null,0,0,null);
            System.out.println("Task \"" + title.get(taskDEL) + "\" deleted succesfully");
            
            //remove every info berkaitan dengan task
            taskNumber.remove(taskDEL);
            title.remove(taskDEL);
            describe.remove(taskDEL);
            date.remove(taskDEL);
            category.remove(taskDEL);
            priority.remove(taskDEL);
            status.remove(taskDEL);
            dependency.remove(taskDEL);
            
            System.out.print("Delete more task? (Enter Yes / No): ");
            String choice = input.nextLine();
            if (!choice.equalsIgnoreCase("yes")) {
                System.out.println("");
                return;
            }
        }
    }

    public static void taskCompletion(ArrayList<Integer> status, ArrayList<Integer> taskNumber, ArrayList<String> taskTitle, int [] dependent, int [] taskPreceding, boolean depend, int count_Depend, Scanner input){
        System.out.println("\n=== Mark Task as Complete ===");
        System.out.print("Enter the task number you want to mark as complete: ");
        int taskDONE = input.nextInt(); //check task dh siap lum //5
        count_Depend = 0;
        switch (status.get(taskDONE-1)){  //status(4)
            case 0:
                for (int j=0;j<dependent.length;j++) { //check whether the task is an dependency task or not 
                    if ((taskDONE-1)==dependent[j]){  //4==dependent[2]  , 4==4
                        depend = true;
                        break;
                    } else {
                        depend = false;
                    }
                    count_Depend++;
                }
                if (depend == false){ //loop for task with no dependency
                    System.out.println("\nTask \"" + taskTitle.get(taskDONE-1) + "\" marked as complete!");
                    status.set(taskDONE-1, 1); //update completion in arraylist
                    actionInDB(3,0,null,null ,null,null,null,0,taskDONE,null,null,0,0,null); //update completion in database
                    break;
                } else { //loop for task with dependency
                    if (status.get(taskPreceding[count_Depend])==0){ //taskStatus[0]==0
                        System.out.println("\nWarning: Task \"" + taskTitle.get(taskDONE-1) + "\" cannot be marked as complete because it depends on \"" + taskTitle.get(taskPreceding[count_Depend])+ "\".");
                        System.out.println("Please complete \"" + taskTitle.get(taskPreceding[count_Depend]) + "\" first."); 
                        break;
                    }else{
                        System.out.println("\nTask \"" + taskTitle.get(taskDONE-1) + "\" marked as complete!");
                        status.set(taskDONE-1, 1); //update completion in arraylist
                        actionInDB(3,0,null,null ,null,null,null,0,taskDONE,null,null,0,0,null); //update completion in database
                        break;
                    }
                }
            case 1:
                System.out.println("\nTask \"" + taskTitle.get(taskDONE-1) + "\" already marked as complete!");
                break;
            default:
                System.out.println("\nNo data for Task " + taskDONE);
        }
    }

    public static void taskDependency(int i_depend, ListNode[] nodes, int[] dependent, int[] taskPreceding, ArrayList<String> title, Scanner input, int taskToEdit) {
        System.out.println("\n=== Set Task Dependency ===");
        System.out.println("\nEnter task number that depends on another task: " + taskToEdit);
        dependent[i_depend] = taskToEdit; // Task that depends on another task
        System.out.print("Enter the task number it depends on: ");
        taskPreceding[i_depend] = input.nextInt(); // Task that is being depended on

        // Add dependency in the linked list (preliminary, before cycle check)
        nodes[taskPreceding[i_depend]-1].next = nodes[dependent[i_depend]-1];

        // Detect cycle of dependency
        for (int k=0; k < dependent.length; k++) {
            TunaMayo cycleDetector = new TunaMayo();
            if (cycleDetector.hasCycle(nodes[taskPreceding[k]])) {
                System.out.println("\nCycle detected! Dependency not added.");
                (nodes[taskPreceding[i_depend]-1]).next = null;  // Remove the cycle dependency
                dependent[i_depend] = 0;  // Reset the last added dependency in tracking arraylist
                taskPreceding[i_depend] = 0;
                break;
            } else {
                // If no cycle detected, add dependency and update the data
                actionInDB(5,0,null,null,null,null,null,0,0,dependent,taskPreceding,i_depend,0,null);
                System.out.println("\nTask \"" + title.get(dependent[i_depend]-1) + "\" now depends on \"" +title.get(taskPreceding[i_depend]-1) + "\".");
                break;
            }
        }
    }
    
    public static void sortTask(ArrayList<Integer> taskNumber, ArrayList<String> title, ArrayList<String> describe, ArrayList<String> date, ArrayList<String> category, ArrayList<String> priority, ArrayList<Integer> status, Scanner input) {
   
    CatchData(taskNumber, title, describe, date, category, priority, status);
    
    int choice;
    int [] priority_rank = new int [priority.size()];
    
    for (int i = 0; i < priority.size(); i++) { //assign value for each priority to make it easier to compare 
        if (priority.get(i).equalsIgnoreCase("low")) 
            priority_rank [i] = 1;
        else if (priority.get(i).equalsIgnoreCase("medium"))
            priority_rank [i] = 2;
        else if (priority.get(i).equalsIgnoreCase("high"))
            priority_rank [i] = 3;
        }
      
    do {
    System.out.println("");
    System.out.println("Sort by :");
    System.out.println("1. Due date (ascending)");
    System.out.println("2. Due date (descending)");
    System.out.println("3. Priority (high to low)");
    System.out.println("4. Priority (low to high)");
    System.out.print("Enter the number of your choice : ");
    while (!input.hasNextInt()) { // Handle non-integer inputs
        System.out.print("Invalid input. Please enter a number between 1 and 4. \nEnter new number : ");
        input.nextLine(); // Consume the invalid input
    }
        choice = input.nextInt();
        input.nextLine(); // Clear the buffer
       
    } while (choice < 1 || choice > 4);
    
    
    switch (choice) {
    
        case 1 : 
        int n = date.size();

        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                LocalDate date1 = LocalDate.parse(date.get(j));
                LocalDate date2 = LocalDate.parse(date.get(j+1));
         
            if (date1.isAfter(date2)) {
                swap1(title, j, j + 1);
                swap1(describe, j, j + 1);
                swap1(date, j, j + 1);
                swap1(category, j, j + 1);
                swap1(priority, j, j + 1);
                swap2(status, j, j + 1);
            }
            }
        }
        break;

        case 2 :
        int k = date.size();
       
        for (int i = 0; i < k-1; i++) {
            for (int j = 0; j < k-i-1; j++) {
                LocalDate date1 = LocalDate.parse(date.get(j));
                LocalDate date2 = LocalDate.parse(date.get(j+1));

            if (date1.isBefore(date2)) {
                swap1(title, j, j + 1);
                swap1(describe, j, j + 1);
                swap1(date, j, j + 1);
                swap1(category, j, j + 1);
                swap1(priority, j, j + 1);
                swap2(status, j, j + 1);
            }
            }
        }
        break;

        case 3 : //sort by priority from high to low
        int m = priority.size();
        for (int i = 0; i < m-1; i++) {
            for (int j = 0; j < m-i-1; j++) {
                    if (priority_rank[j] < priority_rank[j+1]) {
                        swap1(title, j, j + 1);
                        swap1(describe, j, j + 1);
                        swap1(date, j, j + 1);
                        swap1(category, j, j + 1);
                        swap1(priority, j, j + 1);
                        swap2(status, j, j + 1);
                    }
                }
            }
        break;

        case 4 :   //sort by priority from low to high
        int h = priority.size();
        for (int i = 0; i < h-1; i++) {
            for (int j = 0; j < h-i-1; j++) {
                    if (priority_rank[j] > priority_rank[j+1]) {
                        swap1(title, j, j + 1);
                        swap1(describe, j, j + 1);
                        swap1(date, j, j + 1);
                        swap1(category, j, j + 1);
                        swap1(priority, j, j + 1);
                        swap2(status, j, j + 1);
                    }
                }
            }
        break;

        default :
        System.out.println("Invalid choice. Please enter 1-4 only.");
            
        } 
  
        for (int i =0; i < taskNumber.size(); i++) {
            String titlesort = title.get(i);
            String describesort = describe.get(i);
            String datesort = date.get(i);
            String categorysort = category.get(i);
            String prioritysort = priority.get(i);
            int statussort = status.get(i);
            
            actionInDB(6, i, titlesort, describesort , datesort, categorysort, prioritysort, 0, 0, null, null, 0, statussort, null);
        }
        
        System.out.println("");
        System.out.println("========Task sorted!========");
        System.out.println("");
        for (int i = 0; i < taskNumber.size(); i++) {
                System.out.print((i+1) + ". " + title.get(i) + " - " + describe.get(i) + " - " + date.get(i) + " - " + category.get(i) + " - " + priority.get(i));
                if (status.get(i)==0) {
                    System.out.println(" - incomplete");
                } else {
                    System.out.println(" - completed");
                }
        }
        System.out.println("");
    }    

    public static void swap1 (ArrayList<String> list, int i, int j){
        String temp1 = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp1);
    }
      
    public static void swap2 (ArrayList<Integer> status, int i, int j){
        int temp2 = status.get(i);
        status.set(i, status.get(j));
        status.set(j, temp2);
    }

    public static void searchTask(ArrayList<String> title, ArrayList<String> describe, ArrayList<String> date, ArrayList<String> category, ArrayList<String> priority, ArrayList<Integer> taskNumber, ArrayList<Integer> status, ArrayList<Integer> dependency, Scanner input) {
    
    CatchData(taskNumber, title, describe, date, category, priority, status);
    
    System.out.println("");  
    System.out.print("Enter your search key : ");
    String keyword = input.nextLine().toLowerCase();
   
    boolean match = false;
    boolean status_key = false;
    
    System.out.println("");
    
    for (int i = 0; i < title.size(); i++) {
        if (keyword.equalsIgnoreCase("incomplete") || keyword.equalsIgnoreCase("unfinished")) { //so that user can search using status jugak
        status_key = (status.get(i) == 0);
        }
        if (keyword.equalsIgnoreCase("complete")  || keyword.equalsIgnoreCase("done") || keyword.equalsIgnoreCase("finish")) {
        status_key = (status.get(i) == 1);
        }
        
        if (title.get(i).toLowerCase().contains(keyword) || 
            describe.get(i).toLowerCase().contains(keyword) || 
            date.get(i).toLowerCase().contains(keyword) ||
            category.get(i).toLowerCase().contains(keyword) ||
            priority.get(i).toLowerCase().contains(keyword) ||
            taskNumber.get(i).toString().toLowerCase().contains(keyword) ||
            status_key ||
            dependency.get(i).toString().toLowerCase().contains(keyword)) {
                match = true;
                System.out.print((i+1) + ". " + title.get(i) + " - " + describe.get(i) + " - " + date.get(i) + " - " + category.get(i) + " - " + priority.get(i));
                if (status.get(i)==0) {
                    System.out.println(" - incomplete");
                } else {
                    System.out.println(" - completed");
                }
        }          
        }
        if (match == false) {
            System.out.println("");
            System.out.println("Sorry, no match found.");
        }  
        System.out.println("");
}

    public static String EmailAddressfromDB(){
        Connection conn1 = null; 
        String address = "";
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db";
            String user = "root";
            String password = "";
            conn1 = DriverManager.getConnection(url1, user, password);
            if (conn1 != null) {
                String sql = "SELECT * FROM user_email";
                var email = conn1.prepareStatement(sql);

                var getEmail = email.executeQuery();
                if (getEmail.next()){
                address = getEmail.getString(1);}
            }
            conn1.close();
        }catch(SQLException ex){
            System.out.println("An error occurred");
            ex.printStackTrace();
        }
        return address;
    }
    
    public static void dueNoti(ArrayList<String> title, ArrayList<String> date, String recepient) throws Exception{
        LocalDateTime currentDate = LocalDateTime.now().withSecond(0).withNano(0); // Current date, hours and minutes
         
        DateTimeFormatter format = DateTimeFormatter.ofPattern("[yyyyMMdd]" + "[yyyy-MM-dd]" + "[yyyy/MM/dd]" + "[yyyy.MM.dd]");
        for (int i_date=0;i_date<date.size();i_date++){
            LocalDate duedate = LocalDate.parse(date.get(i_date), format);
            LocalDateTime due = duedate.atStartOfDay(); 
            if (due.minusDays(1).compareTo(currentDate)==0){ //check if its already 00:00 of a day before due date.
                System.out.println("=== Email Notification ===");
                System.out.println("Sending reminder email for task "+ title.get(i_date) + " due in 24 hours.");
                TunaMayowithEmail.sendMailNoti(recepient, title, i_date);
                System.out.println("");
            }
        }
    }
    
    public static void dataAnalytics(int i, ArrayList<Integer> taskNumber, ArrayList<Integer> status, ArrayList<String> category){
        System.out.println("\n=== Analytics Dashboard ===");
        int taskInR = RecurringTask.getNumTaskDB();
        int totalTask = i+taskInR;
        System.out.println("> Total task: " + totalTask);
        int count=0, hw=0, personal=0, work=0;
        for (int tm=0; tm<taskNumber.size();tm++){
            if (status.get(tm)==1){
                count++;
            }
        }
        int completeInR = RecurringTask.getTotalComplete();
        int totalComplete = count + completeInR;
        System.out.println("> Completed: " + totalComplete);
        System.out.println("> Pending: " + (totalTask-totalComplete));
        System.out.printf("> Completion Rate: %.2f" , (((double)totalComplete/totalTask)*100.0));
        for (int tm=0;tm<category.size();tm++){
            if (category.get(tm).equalsIgnoreCase("homework")){
                hw++;
            } else if (category.get(tm).equalsIgnoreCase("personal")){
                personal++;
            } else if (category.get(tm).equalsIgnoreCase("work")){
                work++;
            }     
        }
        System.out.print("% \n> Task Categories:- Homework: "+ hw +", Personal: "+ personal +", Work: "+ work);
    }
    
    //this method ni khas for action2 nk dibuat kat db;
    public static void actionInDB(int actforDB, int i, String title, String describe , String date, String category, String priority, int taskDEL, int taskDONE, int [] dependent, int [] taskPreceding, int i_depend, int status, String recepient){
        Connection conn1 = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn1 = DriverManager.getConnection(url1, user, password);
            if (conn1 != null) {
                switch (actforDB){
                    case 1:
                        String sql1 = "insert into task(numTask, Title, Description, Due, Category, Priority) values (?,?,?,?,?,?)";
                        var myStat = conn1.prepareStatement(sql1);

                        myStat.setInt(1, i);
                        myStat.setString(2, title);
                        myStat.setString(3, describe);
                        myStat.setString(4, date);
                        myStat.setString(5, category);
                        myStat.setString(6, priority);

                        myStat.executeUpdate();
                        break;
                    case 2:
                        String sql2 = "DELETE FROM task WHERE numTask=?";
                        myStat = conn1.prepareStatement(sql2);

                        myStat.setInt(1, taskDEL);
                        
                        myStat.execute();
                        break;
                    case 3:
                        String sql3 = "UPDATE task SET Completion=1 WHERE numTask=?";
                        var update = conn1.prepareStatement(sql3);

                        update.setInt(1, (taskDONE-1));

                        update.executeUpdate();
                        break;
                    case 4:
                        System.out.println("\n=== View All Tasks ===");
                        String sql4 = "SELECT * FROM task where numTask>=0";
                        var check = conn1.prepareStatement(sql4);

                        var results = check.executeQuery();
                        String statusTask = "";
                        while (results.next()){
                            if (results.getInt(7)==0){
                                statusTask = "Incomplete";
                            } else {
                                statusTask = "Completed";
                            }

                            if (results.getInt(8) == -1){ //output for task with no dependency
                                System.out.println((results.getInt(1)+1) + ". [" + statusTask +  "] " + results.getString(2) + " - Due: " + results.getDate(4));    
                            } else { //output for task with dependency
                                System.out.println((results.getInt(1)+1) + ". [" + statusTask +  "] " + results.getString(2) + " - Due: " + results.getDate(4) + " (Depends on Task " + (results.getInt(8)+1) + ")");    
                            }
                        } 
                        System.out.println("");
                        break;
                    case 5:
                        String sql5 = "UPDATE task SET Dependency=? WHERE numTask=?";
                        update = conn1.prepareStatement(sql5);

                        update.setInt(1, taskPreceding[i_depend]-1);
                        update.setInt(2, dependent[i_depend]-1);

                        update.executeUpdate();
                        break;
                    
                    case 6 :
                        String sql6 = "UPDATE task SET Title = ?, Description = ?, Due = ?, Category = ?, Priority = ?, Completion = ? WHERE numTask = ?";
                        update = conn1.prepareStatement(sql6);
                
                        update.setString(1, title);
                        update.setString(2, describe);
                        update.setString(3, date);
                        update.setString(4, category);
                        update.setString(5, priority);
                        update.setInt(6, status);
                        update.setInt(7, i);

                        update.executeUpdate();
                        break;
                    case 7:
                        String sql7 = "UPDATE user_email SET emailAddress=?";
                        update = conn1.prepareStatement(sql7);

                        update.setString(1, recepient);

                        update.executeUpdate();
                        break;
                    case 8: //edit title
                        String sql8 = "UPDATE task SET Title = ? WHERE numTask = ?";
                        update = conn1.prepareStatement(sql8);
                
                        update.setString(1, title);
                        update.setInt(2, i);
                        update.executeUpdate();
                        break;
                    case 9: //edit title
                        String sql9 = "UPDATE task SET Description = ? WHERE numTask = ?";
                        update = conn1.prepareStatement(sql9);
                
                        update.setString(1, describe);
                        update.setInt(2, i);
                        update.executeUpdate();
                        break;
                    case 10: //edit title
                        String sql10 = "UPDATE task SET Due = ? WHERE numTask = ?";
                        update = conn1.prepareStatement(sql10);
                
                        update.setString(1, date);
                        update.setInt(2, i);
                        update.executeUpdate();
                        break;
                    case 11:
                        String sql11 = "UPDATE task SET Category = ? WHERE numTask = ?";
                        update = conn1.prepareStatement(sql11);
                
                        update.setString(1, category);
                        update.setInt(2, i);
                        update.executeUpdate();
                        break;
                    case 12:
                        String sql12 = "UPDATE task SET Priority = ? WHERE numTask = ?";
                        update = conn1.prepareStatement(sql12);
                
                        update.setString(1, priority);
                        update.setInt(2, i);
                        update.executeUpdate();
                        break;
                } 
            }
        } catch(SQLException ex){
            System.out.println("An error occurred");
            ex.printStackTrace();
        } finally {
            if (conn1 != null) {
                try {
                    conn1.close();
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        } 
    }

    public static void CatchData(ArrayList<Integer> taskNumber, ArrayList<String> title, ArrayList<String> describe, ArrayList<String> date, ArrayList<String> category, ArrayList<String> priority, ArrayList<Integer> status){
       // to makse sure the arraylist di clear kan before fetching the database or else dia akan duplicate
        taskNumber.clear();
        title.clear();
        describe.clear();
        date.clear();
        category.clear();
        priority.clear();
        status.clear();
    
        Connection conn1 = null;
        PreparedStatement p = null;
        ResultSet rs = null; 
     
        try {
        
        // Importing and registering drivers
        String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
        String user = "root";
        String password = "";

        //connect with your database
        conn1 = DriverManager.getConnection(url1, user, password);
        
        //command to select data
        String sql="select * from task";
        p = conn1.prepareStatement(sql);
        rs = p.executeQuery();
        
        //fetch until there is no data
        while (rs.next()) {
            
            //update data to arraylist
            taskNumber.add (rs.getInt("numTask"));
            title.add (rs.getString("Title"));
            describe.add (rs.getString("Description"));
            date.add (rs.getString("Due"));
            category.add (rs.getString("Category"));
            priority.add (rs.getString("Priority"));
            status.add (rs.getInt("Completion"));
        }
        }catch (SQLException e) {
            System.out.println(e);
     
        }
    }
    
    // Method to detect cycle of dependencies using the slow and fast pointer approach
    public static boolean hasCycle(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;

        // Traverse the linked list using fast and slow pointers
        while (fast != null && fast.next != null) {
            fast = fast.next.next; // Move fast pointer two steps
            slow = slow.next; // Move slow pointer one step

            // If fast and slow pointers meet, a cycle is detected
            if (fast == slow) {
                return true;
            }
        }
        // No cycle detected
        return false;
    }
}
