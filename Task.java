package recurringtask;

public class Task {

    public String title;
    public String description;
    public int k;
    
    public Task(int numTask, String title,String description){
        this.title = title;
        this.description = description;
        this.k = numTask;
    }
    
    public int getNumTask(){
        return k;
    }
    
    public String getTitle(){
        return title;
    }
    
    public String getDescription(){
        return description;
    }
    
    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }
    
    public void updateNumTask(int newNumTask) {
        this.k = newNumTask;
    }
    public static void main(String[] args) {
        
    }
}