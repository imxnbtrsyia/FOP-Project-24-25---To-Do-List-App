package recurringtask;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

public class RecurringTask extends Task {
    public static String recurrenceInterval; //"weekly", "daily", "monthly"
    public static LocalDate nextOccurence;
    public static LocalDate due;
    public static java.sql.Date sqlDue;
    public static java.sql.Date sqlnextOcc;
    public static ArrayList<Task> taskList = new ArrayList<>();
    public static int i;
    public static int complete;
    public static int currentNumTask;
    
    public RecurringTask(int numTask, String title, String description, String recurrenceInterval, LocalDate nextOccurence, LocalDate due, int complete) {
    super(numTask, title, description);
    this.i = numTask;
    this.recurrenceInterval = recurrenceInterval;
    this.nextOccurence = LocalDate.now();
    this.due = calculateDueDate(nextOccurence, recurrenceInterval);
    this.complete = complete;
    }
   
    public static void main(String[] args) {
        actionInDBR(1, 0, null, null, null, 0);
        scheduleRecurringTaskCheck();
        Scanner scanner = new Scanner(System.in);
        int menu;
        int numTask;
        do {
            System.out.println("1. Add recurring tasks");
            System.out.println("2. Task completion");
            System.out.println("3. Display task");
            System.out.println("4. Exit");
            System.out.print("Enter number: \n");
            menu = scanner.nextInt();

            switch(menu) {
                case 1:
                    System.out.println("\n=== Add a Recurring Task ===");
                   //addRecurringTask();
                   
                    break;
                case 2:
                    //displayTaskTitle();
                    if (taskList.isEmpty()){
                        break;
                    }
                    System.out.print("Which task do you want to mark as completed? (Insert number): \n");
                    numTask = scanner.nextInt();
                    completeTask(numTask-1);
                    
                    break;
                    case 3:
                    //displayTask();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Incorrent input.");

            }
    } while (menu != 4);
    }
    
    public void updateRecurrenceInterval(String newRecurrenceInterval) {
        this.recurrenceInterval = newRecurrenceInterval;
        this.nextOccurence = LocalDate.now(); // Recalculate next occurrence
        switch (newRecurrenceInterval.toLowerCase()) {
            case "daily":
                this.due = nextOccurence.plusDays(1);
                break;
            case "weekly":
                this.due = nextOccurence.plusWeeks(1);
                break;
            case "monthly":
                this.due = nextOccurence.plusMonths(1);
                break;
    }
        this.sqlnextOcc = java.sql.Date.valueOf(nextOccurence);
        this.sqlDue = java.sql.Date.valueOf(due);
    }
    
       public void updateCompletion(int newCompletion) {
        this.complete = newCompletion;
    }
   
    public static void scheduleRecurringTaskCheck() {
        // Create a ScheduledExecutorService that runs the update task periodically
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Schedule the update task every 1 minute (you can change this to any interval)
        scheduler.scheduleAtFixedRate(() -> {
            // Call the method that checks and updates task completion
            actionInDBR(6, 0, null, null, null, 1);
        }, 0, 1, TimeUnit.MINUTES);  // Initial delay of 0, repeat every 1 minute
        
    }
        
    public LocalDate calculateDueDate(LocalDate nextOccurence, String recurrenceInterval) {
    switch (recurrenceInterval.toLowerCase()) {
        case "daily":
            return nextOccurence.plusDays(1);
        case "weekly":
            return nextOccurence.plusWeeks(1);
        case "monthly":
            return nextOccurence.plusMonths(1);
        default:
            throw new IllegalArgumentException("Invalid recurrence interval: " + recurrenceInterval);
    }
}

    public static String getRecurrenceInterval(int taskId){ //REMAIN KAT LUAR
        Connection conn = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                String sql = "SELECT RecurrenceInterval FROM recurtask WHERE numTask = ? ";
                var myStat = conn.prepareStatement(sql);
                myStat.setInt(1, taskId);
                ResultSet rs = myStat.executeQuery();
                if(rs.next()){
                String recurInterval = rs.getString("RecurrenceInterval");
                return recurInterval;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (conn != null) 
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            
            }
        }
        return null;
    }  
    
        public static java.sql.Date getNextOccurence(int taskId){ //REMAIN KAT LUAR
        Connection conn = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                String sql = "SELECT NextOccurence FROM recurtask WHERE numTask = ? ";
                var myStat = conn.prepareStatement(sql);
                myStat.setInt(1, taskId);
                ResultSet rs = myStat.executeQuery();
                if(rs.next()){
                java.sql.Date sqlDate = rs.getDate("NextOccurence");
                
                return sqlDate;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (conn != null) 
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            
            }
        }
        return null;
    }  
        
        public static java.sql.Date getDueDate(int taskId){ //REMAIN KAT LUAR
        Connection conn = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                String sql = "SELECT Due FROM recurtask WHERE numTask = ? ";
                var myStat = conn.prepareStatement(sql);
                myStat.setInt(1, taskId);
                ResultSet rs = myStat.executeQuery();
                if(rs.next()){
                java.sql.Date sqlDate = rs.getDate("Due");
                
                return sqlDate;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (conn != null) 
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            
            }
        }
        return null;
    }  
        
        public static int getCompletion(int taskId){ //REMAIN KAT LUAR
        Connection conn = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                String sql = "SELECT Completion FROM recurtask WHERE numTask = ? ";
                var myStat = conn.prepareStatement(sql);
                myStat.setInt(1, taskId);
                ResultSet rs = myStat.executeQuery();
                if(rs.next()){
                int numTask = rs.getInt("Completion");
                return numTask;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (conn != null) 
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            
            }
        }
        return 0;
    }  
        
    public static String getTitleDB(int taskId){ //REMAIN KAT LUAR
        Connection conn = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                String sql = "SELECT Title FROM recurtask WHERE numTask = ? ";
                var myStat = conn.prepareStatement(sql);
                myStat.setInt(1, taskId);
                ResultSet rs = myStat.executeQuery();
                if(rs.next()){
                String title1 = rs.getString("Title");
                
                return title1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (conn != null) 
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            
            }
        }
        return null;
    }  
    
        public static int getNumTaskDBEDit(int taskId){ //REMAIN KAT LUAR
        Connection conn = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                String sql = "SELECT numTask FROM recurtask WHERE numTask = ? ";
                var myStat = conn.prepareStatement(sql);
                myStat.setInt(1, taskId);
                ResultSet rs = myStat.executeQuery();
                if(rs.next()){
                int numTask = rs.getInt("numTask");
                return numTask;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (conn != null) 
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            
            }
        }
        return 0;
    }  

        public static int getNumTaskDB(){ //this is total
        Connection conn = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            int numTask = 0;
            //connect with your database
            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                String sql = "SELECT numTask FROM recurtask WHERE ? ";
                var myStat = conn.prepareStatement(sql);
                myStat.setInt(1, 1);
                ResultSet rs2 = myStat.executeQuery();
                while (rs2.next()) {
                    numTask = rs2.getInt("numTask");
                }
                return numTask;
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (conn != null) 
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            
            }
        }
        return 0;
    }  

    public static boolean taskAlreadyComplete(int numTask){
         Connection conn = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
    
                String sql8 = "SELECT Completion FROM recurtask WHERE numTask = ?";
                var myStat2 = conn.prepareStatement(sql8);
                myStat2.setInt(1, numTask);
                ResultSet rs = myStat2.executeQuery();

            if (rs.next()) {
                int isCompleted = rs.getInt("Completion");
                if (isCompleted == 1){
                    return true;
                } else {
                    return false;
                }
                    }
            }
    }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (conn != null) 
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            
            }
        }
        return false;
    }  
    
    public static void addRecurringTask(int numTask){
        Scanner scanner = new Scanner(System.in);
        String interval;
        int taskcomplete = 0;
        
        System.out.println("Enter task title: ");
        String title = scanner.nextLine();
        
        System.out.println("Enter task description: ");
        String description = scanner.nextLine();
        
        inputInterval: 
            while (true){
            System.out.println("Enter task recurrence interval (daily, weekly, monthly): ");
            interval = scanner.next();
            if (interval.equalsIgnoreCase("daily")||interval.equalsIgnoreCase("weekly")||interval.equalsIgnoreCase("monthly"))
                break inputInterval;
            else {
               System.out.println("Invalid interval.");
               continue inputInterval;
                }
        }
        
        
       nextOccurence = LocalDate.now();
       
        switch (interval.toLowerCase()){
            case "daily":
                due = nextOccurence.plusDays(1);
                break;
            case "weekly":
                due = nextOccurence.plusWeeks(1);
                break;
            case "monthly":
                due = nextOccurence.plusMonths(1);
                break;
            default:
                throw new IllegalArgumentException ("Invalid recurrence interval.");
        }
        
        sqlnextOcc = java.sql.Date.valueOf(nextOccurence);
        sqlDue = java.sql.Date.valueOf(due);

        RecurringTask recurringTask = new RecurringTask(numTask, title, description, interval, nextOccurence, due, taskcomplete);
        taskList.add(recurringTask);
        actionInDBR(2, 0, title, description, interval, 0);
 
        System.out.println("\nRecurring Task '"+ title +"' created successfully!\n");
    }
        
    public static void completeTask(int taskId){
        if (taskId < 0 || taskId >= taskList.size()){
            System.out.println("Invalid task ID.\n");
            return;
        }
        
        Task task = taskList.get(taskId); //0
        
        if (task instanceof RecurringTask) {
            RecurringTask recurringTask = (RecurringTask) task; 
            complete = 1;
            actionInDBR(4, taskId, null, null, null, complete); //calculate nextocc due and update terus
            System.out.println("\nRecurring Task '" + getTitleDB(taskId) + "' completed!\n");
        } else {
            System.out.println("\nTask '" + task.getTitle() +"' completed!\n");
            taskList.remove(taskId); //Non-recurring tasks are removed
        }
        
    }
    
      public static void deleteTask(int taskId){
          
            Task task = taskList.get(taskId);
            String title = getTitleDB(taskId);
            actionInDBR(8,taskId,null,null ,null, 0);
            
            if (task instanceof RecurringTask) {
                RecurringTask recurringTask = (RecurringTask) task;
                System.out.println("\nRecurring Task '" + title + "' deleted!\n"); //isu kt sini pulak title ahhh
        } for (int j = 0; j < taskList.size(); j++) {
            if (taskList.get(j).getNumTask() == taskId) {
                taskList.remove(j);
                break;
            }
        }
            //reindexTasksInDatabase(connection);
            actionInDBR(12, 0, null, null, null, 0);
        }
    
     public static void actionInDBR(int actforDB, int taskId, String title, String description, String interval, int taskcomplete){
        Connection conn1 = null; 
        try {
            String url1 = "jdbc:mysql://localhost:3306/tunamayo_db"; //insert your database name
            String user = "root";
            String password = "";
            
            //connect with your database
            conn1 = DriverManager.getConnection(url1, user, password);
            if (conn1 != null) {
                switch (actforDB){
                    case 1: //load database 
                        String sql = "SELECT numTask, Title, Description, RecurrenceInterval, NextOccurence, Due, Completion FROM recurtask"; // Adjust table/column names
                        
                        PreparedStatement stmt = conn1.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery(); 

                        while (rs.next()) {
                            int numTask = rs.getInt("numTask");
                            String title1 = rs.getString("title");
                            String description1 = rs.getString("description");
                            String recurrence = rs.getString("RecurrenceInterval");
                            java.sql.Date nextocc = rs.getDate("NextOccurence");
                            java.sql.Date duedate = rs.getDate("Due");
                            int isCompleted = rs.getInt("completion");

                            LocalDate nextocc2 = nextocc.toLocalDate();
                            LocalDate duedate2 = duedate.toLocalDate();

                        int j = 1;
                        RecurringTask task = new RecurringTask(j, title1, description1, recurrence, nextocc2, duedate2, isCompleted); // Assuming Task has this constructor
                        taskList.add(task); 
                    
                        i = Math.max(i, numTask + 1);
                        j++;
                        }
                        System.out.println("Tasks loaded from database successfully!");
                        break;
                    case 2: //addrecurringtask -> INSERT DATA
                        nextOccurence = LocalDate.now();
        
                        switch (interval.toLowerCase()){
                            case "daily":
                                due = nextOccurence.plusDays(1);
                                break;
                            case "weekly":
                                due = nextOccurence.plusWeeks(1);
                                break;
                            case "monthly":
                                due = nextOccurence.plusMonths(1);
                                break;
                            default:
                                throw new IllegalArgumentException ("Invalid recurrence interval.");
                        }

                        sqlDue = java.sql.Date.valueOf(due);
                        sqlnextOcc = java.sql.Date.valueOf(nextOccurence);
                        
                        String sql2 = "INSERT into recurtask(numTask, Title, Description, RecurrenceInterval, NextOccurence, Due, Completion) values (?,?,?,?,?,?,?)";
                        var myStat = conn1.prepareStatement(sql2);

                        myStat.setInt(1, i);
                        myStat.setString(2, title);
                        myStat.setString(3, description);
                        myStat.setString(4, interval);
                        myStat.setDate(5, sqlnextOcc);
                        myStat.setDate(6, sqlDue);
                        myStat.setInt(7, taskcomplete);

                        myStat.executeUpdate();

                        break;
                    case 3: //displayTaskTitle
                        if (taskList.isEmpty()){
                            System.out.println("No tasks available.");
                        return;
                        }
        
                        String sql3 = "SELECT numTask, Title FROM recurtask"; // Adjust table/column names
                        PreparedStatement stmt1 = conn1.prepareStatement(sql3);
                        ResultSet rs1 = stmt1.executeQuery();

                        while (rs1.next()) {
                            int numTask = rs1.getInt("numTask");
                            String title2 = rs1.getString("title");

                            System.out.println("");
                            System.out.print(numTask+1 + ". " + title2);
                            }
                        break;
                    case 4: //updatenextoccurence
                        java.sql.Date sqlDate = getNextOccurence(taskId);
                        
                        LocalDate nextOcc = sqlDate.toLocalDate();
                        LocalDate dueDate;
                        Task task = taskList.get(taskId); 
                        RecurringTask recurringTask = (RecurringTask) task;
                        
                        switch (recurringTask.getRecurrenceInterval(taskId).toLowerCase()){
                            case "daily":
                                nextOcc = nextOcc.plusDays(1);
                                dueDate = nextOcc.plusDays(1);
                                
                                break;
                            case "weekly":
                                nextOcc = nextOcc.plusWeeks(1);
                                dueDate = nextOcc.plusWeeks(1);
                                
                                break;
                            case "monthly":
                                nextOcc = nextOcc.plusMonths(1);
                                dueDate = nextOcc.plusMonths(1);
                                
                                break;
                            default:
                                throw new IllegalArgumentException ("Invalid recurrence interval.");
                        }

                        sqlnextOcc = java.sql.Date.valueOf(nextOcc);
                        sqlDue = java.sql.Date.valueOf(dueDate);

                        String sql4 = "UPDATE recurtask SET NextOccurence = ?, Due = ?, Completion = ? WHERE numTask = ?";

                        // Prepare the SQL statement
                        PreparedStatement preparedStatement = conn1.prepareStatement(sql4);

                        // Set the parameter values
                        preparedStatement.setDate(1, sqlnextOcc);
                        preparedStatement.setDate(2, sqlDue); 
                        preparedStatement.setInt(3, taskcomplete); 
                        preparedStatement.setInt(4, taskId); 

                        // Execute the update
                        preparedStatement.executeUpdate();

                        break;
                    case 5: //display task
                        if (taskList.isEmpty()){
                            System.out.println("No tasks available.");
                        return;
                        }
                        String sql5 = "SELECT numTask, Title, Due, Completion FROM recurtask"; // Adjust table/column names
                        PreparedStatement stmt2 = conn1.prepareStatement(sql5);
                        ResultSet rs2 = stmt2.executeQuery();

                        while (rs2.next()) {
                            int numTask = rs2.getInt("numTask");
                            String title3 = rs2.getString("title");
                            java.sql.Date duedate = rs2.getDate("Due");
                            int isCompleted = rs2.getInt("completion");

                            LocalDate duedate2 = duedate.toLocalDate();

                            System.out.println("");
                            System.out.print(numTask+1 + ". ");
                            if (isCompleted == 0)
                                System.out.print("[Incomplete] "+title3+" - Due: "+duedate2);
                            else if (isCompleted == 1)
                                System.out.print("[Complete] "+title3+" - Due: "+duedate2);
                        }
                        break;
                    case 6: //select update
                        LocalDate today = LocalDate.now();
                        String sql7 = "SELECT numTask, NextOccurence, Completion FROM recurtask";
                        var myStat1 = conn1.prepareStatement(sql7);
                        ResultSet rs3 = myStat1.executeQuery();

                    while (rs3.next()) {
                        int numTask = rs3.getInt("numTask");
                        java.sql.Date nextocc = rs3.getDate("NextOccurence");
                        int isCompleted = rs3.getInt("Completion");

                        LocalDate nextocc2 = nextocc.toLocalDate();

                        if (nextocc2.isEqual(today) && isCompleted == 1){
                            isCompleted = 0;
                            actionInDBR(7, numTask, null, null, null, isCompleted);
                        }
                    }
                break;
                    case 7: //update completion when today == nextocc
                        String sql6 = "UPDATE recurtask SET Completion = ? WHERE numTask = ?";
                        // Prepare the SQL statement
                        preparedStatement = conn1.prepareStatement(sql6);

                        // Set the parameter values
                        preparedStatement.setInt(1, taskcomplete);
                        preparedStatement.setInt(2, taskId); 

                        // Execute the update
                        preparedStatement.executeUpdate();  
                        break;
                    case 8: //delete n update numTask ascending
                        String sql9 = "DELETE FROM recurtask WHERE numTask=?";
                        myStat = conn1.prepareStatement(sql9);

                        myStat.setInt(1, taskId);
                        
                        myStat.execute();
                        //update numTask ascending in database
                        
                        break;
                    case 9:
                        String sql8 = "UPDATE recurtask SET Title = ? WHERE numTask = ?";
                        var update = conn1.prepareStatement(sql8);
                
                        update.setString(1, title);
                        update.setInt(2, taskId);
                        update.executeUpdate();
                        break;
                    case 10:
                        String sql10 = "UPDATE recurtask SET Description = ? WHERE numTask = ?";
                        var update10 = conn1.prepareStatement(sql10);
                
                        update10.setString(1, description);
                        update10.setInt(2, taskId);
                        update10.executeUpdate();
                        break;
                    case 11:
                        java.sql.Date DateNextOcc = getNextOccurence(taskId);
                        
                        LocalDate newNextOcc = DateNextOcc.toLocalDate();
                        LocalDate newDue = null;
                        
                        switch (interval.toLowerCase()) {
                            case "daily":
                                newDue = newNextOcc.plusDays(1);
                                break;
                            case "weekly":
                                newDue = newNextOcc.plusWeeks(1);
                                break;
                            case "monthly":
                                newDue = newNextOcc.plusMonths(1);
                                break;
                            }
                        
                            java.sql.Date sqlnewNextOcc = java.sql.Date.valueOf(newNextOcc);
                            java.sql.Date sqlnewDue = java.sql.Date.valueOf(newDue);
                        String sql11 = "UPDATE recurtask SET RecurrenceInterval = ?, NextOccurence = ?, Due = ? WHERE numTask = ?";
                        var update11 = conn1.prepareStatement(sql11);
                
                        update11.setString(1, interval);
                        update11.setDate(2,sqlnewNextOcc);
                        update11.setDate(3,sqlnewDue);
                        update11.setInt(4, taskId);
                        update11.executeUpdate();
                        break;
                       
                    case 12: //reindex numTask in 
                        String select = "SELECT numTask FROM recurtask"; // Adjust table/column names
                        PreparedStatement stmt4 = conn1.prepareStatement(select);
                        ResultSet rs4 = stmt4.executeQuery();
                        String updateQuery = "UPDATE recurtask SET numTask = ? WHERE numTask = ?";
                        PreparedStatement stmt3 = conn1.prepareStatement(updateQuery);
                        
                        int inc = 0;
                        while (rs4.next()) {
                            int numTask = rs4.getInt("numTask"); //2
                            //for (int j = 0; j < taskList.size(); j++) {
                                Task task1 = taskList.get(inc);
                                task1.updateNumTask(inc); // Set numTask in the taskList to 0-based index

                                // Update the database
                                stmt3.setInt(1, inc);
                                if (numTask > inc)// New numTask value (0-based)
                                    stmt3.setInt(2, inc+1); // Current numTask in the database
                                else
                                    stmt3.setInt(2, inc);
                                
                                stmt3.executeUpdate();
                                inc++;
}
                        
                    break;
                    case 13: //restart tarikh next occ ngn due
                        LocalDate restart = LocalDate.now();
                        LocalDate dueRestart = null;
                        
                        switch(getRecurrenceInterval(taskId).toLowerCase()){
                            case "daily":
                                dueRestart = restart.plusDays(1);
                                break;
                            case "weekly":
                                dueRestart = restart.plusWeeks(1);
                                break;
                            case "monthly":
                                dueRestart = restart.plusMonths(1);
                                break;
                            }
                
                        java.sql.Date sqlRestart = java.sql.Date.valueOf(restart);
                        java.sql.Date sqldueRestart = java.sql.Date.valueOf(dueRestart);
                        
                        String sql14 = "UPDATE recurtask SET NextOccurence = ?, Due = ? WHERE numTask = ?";
                        var update1 = conn1.prepareStatement(sql14);
                
                        update1.setDate(1, sqlRestart);
                        update1.setDate(2, sqldueRestart);
                        update1.setInt(3, taskId);
                        update1.executeUpdate();
                        break;
            } 
          }
        }catch(SQLException ex){
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
}


