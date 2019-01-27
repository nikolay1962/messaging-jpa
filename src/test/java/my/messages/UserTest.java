package my.messages;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    User user;

    @Before
    public void setUp() throws Exception {
        user = new User("email", "name", 11, "pAssWoRd", 1234567890L, 0, null);
    }

    @Test
    public void getEmail_ReturnsCorrectValue() {
        String email = user.getEmail();
        assertEquals("email", email);
    }

    @Test
    public void getName_ReturnsCorrectValue() {
        String name = user.getName();
        assertEquals("name", name);
    }

    @Test
    public void getAge_ReturnsCorrectValue() {
        int age = user.getAge();
        assertEquals(11, age);
    }

    @Test
    public void getPassword_ReturnsCorrectValue() {
        String password = user.getPassword();
        assertEquals("pAssWoRd", password);
    }

    @Test
    public void getlastLogoutTime_ReturnsCorrectValue() {
        user.setLastLogoutTime("2018-09-07T22:22:22.222");
        String lastLogoutTime = user.getlastLogoutTime();
        assertEquals("2018-09-07T22:22:22.222", lastLogoutTime.toString());
    }

    @Test
    public void setLastLogoutTime_ReturnsCorrectValue() {
        // actally it was tested in getlastLogoutTime_ReturnsCorrectValue
    }

    @Test
    public void getLastTimeOfMessageGet_ReturnsCorrectValue() {
        long lastTimeOfMessageGet = user.getLastTimeOfMessageGet();
        assertEquals(1234567890L, lastTimeOfMessageGet);
    }

    @Test
    public void setLastTimeOfMessageGet_ReturnsCorrectValue() {
        long lastTimeOfMessageGet = user.getLastTimeOfMessageGet();
        user.setLastTimeOfMessageGet(9876543210L);
        assertEquals(9876543210L, user.getLastTimeOfMessageGet());
        user.setLastTimeOfMessageGet(lastTimeOfMessageGet);
    }

    @Test
    public void getChats_ReturnsCorrectValue() {
        //one chat must be in a list
        assertEquals(1, user.getChats().size());
    }

    @Test
    public void toString_ReturnsCorrectValue() {
        user.setLastLogoutTime("2018-09-07T22:22:22.222");
        String userToString = "User{email='email', name='name', age=11, password='pAssWoRd', lastLogoutTime=2018-09-07T22:22:22.222}";
        assertEquals(userToString, user.toString());
    }
}