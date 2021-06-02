import java.io.File;
import java.util.Date;

public class TestClass {
    public static void main(String[] args) throws InterruptedException {
        File file = new File("C:\\Users\\DNS\\IdeaProjects\\ShareColudStorage\\ServersStorage\\admin\\Dmitry.txt");
        File file1 = new File("C:\\test\\Dmitry.txt");
        Date date = new Date();
        long modification = date.getTime();
        System.out.println(modification);
        System.out.println(file.lastModified());
        System.out.println(file1.lastModified());
        System.out.println(file.lastModified() == file1.lastModified());
        System.out.println("Set modification");
        file.setLastModified(modification);
        file1.setLastModified(modification);
        System.out.println(file.lastModified() == file1.lastModified());
    }
}
