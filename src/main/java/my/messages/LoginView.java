package my.messages;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;

public class LoginView implements MyChatViewsInterface {

    private UserServices userServices;

    private IOUtils ioUtils;

    private boolean proceed;

    private final SessionFactory sessionFactory;


    public LoginView(boolean withDataBase) {

        this.sessionFactory = withDataBase ? getSessionFactory() : null;
        this.ioUtils = new IOUtils(this.sessionFactory);
        this.userServices = new UserServices(ioUtils);
        this.proceed = true;

    }

    @Override
    public void show(User user) {
        while (this.proceed) {
            printMenu(user);
            String choice = ioUtils.getInputFromUser();
            processUserInput(choice);
        }

        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Override
    public void processUserInput(String choice) {
        switch (choice) {
            case "1":
                this.proceed = false;
                break;

            case "2":
                userServices.addUser();
                break;

            case "3":
                try {
                    userServices.login();
                } catch (IOException e) {
                    ioUtils.writeMessage("LoginView: IOException while trying to login.");
                }
                break;
        }
    }

    private SessionFactory getSessionFactory() {
        // Create registry
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    private void printMenu(User currentUser) {
//        String whoAmI = currentUser == null ? "Unknown user" : currentUser.getName();
        System.out.println("1 - Exit");
        System.out.println("2 - Sign Up (new user)");
        System.out.println("3 - Login");
        System.out.println("Please, enter your choice, mr. Unknown");
    }

}
