package ticket.booking.app.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.app.entities.Ticket;
import ticket.booking.app.entities.User;
import utils.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;

public class UserBookingService {
  private User user;
  private List<User> userList;

  private static final String USERS_PATH = "app/src/main/java/ticket/booking/app/localDB/users.json";
  private static final String TICKET_PATH= "app/src/main/java/ticket/booking/app/entities/Ticket.java";


  private ObjectMapper objectMapper = new ObjectMapper();

  private Predicate<User> userNameTaken() {
    return existingUser -> existingUser.getName().equalsIgnoreCase(user.getName());
  }


  public UserBookingService(User user) throws IOException {
    this.user = user;
    File users = new File(USERS_PATH);
    userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
  }

  public Boolean loginUser() {
    Optional<User> foundUser = userList.stream().filter(existingUser -> {
      return existingUser.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),existingUser.getHashedPassword());
    }).findFirst();

    // check if password is correct

    return  foundUser.isPresent();
  };

  private void saveUserToDB() throws IOException{
    File userFile = new File(USERS_PATH);
    objectMapper.writeValue(userFile,userList);
  }

//  private void saveTicketToDB(ticket) throws IOException {
//
//  }

  public Boolean signUpUser (User newUser){
    try {
      // check if user already exist
      Optional<User> userExist = userList.stream().filter(userNameTaken()).findFirst();

      if(userExist.isPresent()){
        System.out.println("Username already taken");
        return  Boolean.FALSE;
      }

      // add new user
      userList.add(newUser);
      // save user to local db
      saveUserToDB();

      return Boolean.TRUE;
    } catch (IOException e) {
      return Boolean.FALSE;
    }
  };

  public void fetchBooking() {
    Optional<User> fetchedUser = userList.stream().filter((existringUser)-> {
      return existringUser.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),existringUser.getHashedPassword());
    }).findFirst();

    fetchedUser.ifPresent(User::printTickets);
  }

  // TODO: Complete this
  public boolean cancelBooking() {
    Scanner s = new Scanner(System.in);
    System.out.println("Enter the ticket id to cancel");
    String ticketId = s.next();

    if(ticketId == null || ticketId.isBlank()){
      System.out.println("Please enter a valid ticket id");
      return Boolean.FALSE;
    }

   boolean removedTicket = user.getTicketBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId));

    if(removedTicket){
      System.out.println("Ticket with id "+ ticketId+ " has been removed");
      return Boolean.TRUE;
    }

    return Boolean.FALSE;
  }
}
