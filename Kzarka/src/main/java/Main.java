import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.Date;
import javax.security.auth.login.LoginException;
import static java.lang.Thread.sleep;
public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String token = "INSERT_TOKEN_HERE"; //Insert the bot's token here
        builder.setToken(token);
        builder.addEventListener(new Main());
        builder.buildAsync();
    }
    //change previousSpawn to edit the spawntime on startup
    long previousSpawn = 1519544113000L;
    Date killdate = new Date(previousSpawn);
    boolean inCombat = false;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //initialising variables
        int ToWindow;
        int ToFinish;
        int WindowMinutes;
        int WindowHours;
        int FinishMinutes;
        int FinishHours;
        String[] a;
        //upon the command !dead, checks to see if kzarka is in window then resets the window's timer.
        if(event.getMessage().getContentRaw().startsWith("!dead")) {
            Date date = new Date();
            long difference = date.getTime() - killdate.getTime();
            ToWindow = 480 - ((int) difference / (1000 * 60));
            if (ToWindow < 0) {
                killdate = new Date();
            }
            event.getMessage().delete().queue();
            inCombat = false;
        }
        //sets the boolean inCombat to true when !incombat is entered
        else if (event.getMessage().getContentRaw().startsWith("!incombat")) {
            inCombat = true;
            event.getChannel().sendMessage("@here Kzarka is now in combat.").queue();
            event.getMessage().delete().queue();
        }
        else if (event.getMessage().getContentRaw().contains("Time until window opens:")){
            //delay messages by 10 seconds to not overspam.
            try {
                sleep(1000*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //prints the message into the console
            System.out.println("We received a message from " +
                    event.getAuthor().getName() + ": " +
                    event.getMessage().getContentDisplay()
            );
            //deletes the previous message
            event.getMessage().delete().queue();
            //sets the long difference which is equal to the milliseconds between the last time kzarka died and the current time
            Date date = new Date();
            long difference = date.getTime() - killdate.getTime();
            ToWindow = 480 - ((int) difference / (1000 * 60));
            ToFinish = 720 - ((int) difference / (1000 * 60));
            //set hours and minutes for each window open and close
            WindowHours = ToWindow / 60;
            WindowMinutes = ToWindow % 60;
            FinishHours = ToFinish / 60;
            FinishMinutes = ToFinish % 60;

            if (inCombat==false) {
                if (ToWindow < 0) {//Displays the message kzarka is in window when the time remaining is less than 0
                    event.getChannel().sendMessage("```js\nCleared at: " + killdate.toString() +
                            "\nCurrent: " + date.toString() +
                            "\n\nTime until window opens: Kzarka is in window." +
                            "\nTime until window closes: " + FinishHours + " Hours and " + FinishMinutes + " Minutes.```").queue();

                } else if (ToFinish < 0) {//Displayed when there is an error and kzarka window has ended without a spawn
                    event.getChannel().sendMessage("```js\nCleared at: " + killdate.toString() +
                            "\nCurrent: " + date.toString() +
                            "\n\nTime until window opens: Kzarka is in window." +
                            "\nTime until window closes: Kzarka's window has closed.```").queue();

                } else {//The normal message when kzarka is not in window
                    event.getChannel().sendMessage("```js\nCleared at: " + killdate.toString() +
                            "\nCurrent: " + date.toString() +
                            "\n\nTime until window opens: " + WindowHours + " Hours and " + WindowMinutes + " Minutes." +
                            "\nTime until window closes: " + FinishHours + " Hours and " + FinishMinutes + " Minutes.```").queue();

                }
            }
            //Displays a message saying Kzarka is in combat when the !incombat command is given
            if (inCombat==true) {
                event.getChannel().sendMessage("```js\nCleared at: " + killdate.toString() +
                        "\nCurrent: " + date.toString() +
                        "\n\nTime until window opens: Kzarka is in combat.```").queue();
            }
        }
        //sets spawn time to the given long in the format milliseconds since epoch
        else if (event.getMessage().getContentRaw().startsWith("!setspawn")) {
            a = event.getMessage().getContentRaw().split(" ");
            previousSpawn = Long.valueOf(a[1]);
            killdate = new Date(previousSpawn);
            event.getMessage().delete().queue();
        }
        //Clear unnecessary messages
        else {
            event.getMessage().delete().queue();
        }
    }
}