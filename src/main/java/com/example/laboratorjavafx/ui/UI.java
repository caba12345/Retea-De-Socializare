package com.example.laboratorjavafx.ui;

import com.example.laboratorjavafx.domain.FriendShip;
import com.example.laboratorjavafx.domain.User;
import com.example.laboratorjavafx.service.Service;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class UI implements UIInterface<UUID>{

    Service service;

    public UI(Service service){
        this.service = service;
    }

    public void run(){
        menu();
        Scanner scanner = new Scanner(System.in);
        int i = scanner.nextInt();
        while(i != 0){
            option(i);
            menu();
            i = scanner.nextInt();
        }
        System.out.println("Exiting...");
    }

    private void menu(){
        System.out.println("Menu");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Show all users");
        System.out.println("6. Show all friendships");
        System.out.println("7. Show number of communities");
        System.out.println("8. Show most sociable community");
        System.out.println("9. Add predefined values");
        System.out.println("10. Show friends from a given month");
        System.out.println(("0. Exit"));
    }

    private void option(int i){
        switch(i){
            case 1:
                //add user
                uiAddUser();
                break;
            case 2:
                //remove user
                uiRemoveUser();
                break;
            case 3:
                //add friendship
                uiAddFriendship();
                break;
            case 4:
                //remove friendship
                uiRemoveFriendship();
                break;
            case 5:
                //show all users
                uiShowAllUsers();
                break;
            case 6:
                //show all friendships
                uiShowAllFriendships();
                break;
            case 7:
                //show number of communities
                uiShowNumberOfCommunities();
                break;
            case 8:
                //show most sociable community
                uiShowMostSociableCommunity();
                break;
            case 9:
                //add predefined values
                uiAddPredefinedValues();
                break;
            case 10:
                //cerinta lab 6
                uiPrieteniLuna();
                break;

            default:
                System.out.println("There is no option with this number!");
        }
    }

    void uiAddUser(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the user's first name: ");
        String firstName = scanner.nextLine();
        System.out.println("Enter the user's last name: ");
        String lastName = scanner.nextLine();
        System.out.println("Enter the user's email: ");
        String email = scanner.nextLine();
        System.out.println("Enter the user's password: ");
        String password = scanner.nextLine();
        User user = new User(firstName, lastName, email, password);
        try{
            service.addUser(user);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void uiRemoveUser(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the user's email: ");
        String email = scanner.nextLine();
        try{
            service.deleteUser(email);
            System.out.println("User removed successfully!");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void uiAddFriendship(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the first user's email: ");
        String email1 = scanner.nextLine();
        System.out.println("Enter the second user's email: ");
        String email2 = scanner.nextLine();
        try{
            service.createFriendship(email1, email2);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void uiRemoveFriendship(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the first user's email: ");
        String email1 = scanner.nextLine();
        System.out.println("Enter the second user's email: ");
        String email2 = scanner.nextLine();
        try{
            service.deleteFriendship(email1, email2);
            System.out.println("Friendship removed successfully!");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void uiShowAllUsers(){
        for(User user : service.getAllUsers()){
            System.out.println(user);
        }
        if(service.getUserCount() == 1){
            System.out.println(service.getUserCount() + " user exists.");
        }
        else System.out.println(service.getUserCount() + " users exist.");
    }

    void uiShowAllFriendships(){
        for(FriendShip friends : service.getAllFriendships()){
            System.out.println(friends);
        }
        if (service.getFriendshipCount() == 1)
            System.out.println(service.getFriendshipCount() + " friendships exists.");
        else{
            System.out.println(service.getFriendshipCount() + " friendships exist.");
        }
    }

    void uiShowNumberOfCommunities(){
        if (service.numberOfCommunities() == 1){
            System.out.println(service.numberOfCommunities() + " community exists.");
        }
        else System.out.println(service.numberOfCommunities() + " communities exist.");
    }

    void uiShowMostSociableCommunity(){
        System.out.println("The most sociable community is: " + service.mostSociableCommunity());
    }

    void uiAddPredefinedValues(){
        try {
            Scanner scanner = new Scanner(System.in);
            service.add_Predefined_Values();
            System.out.println("Predefined values added successfully!");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public void uiPrieteniLuna(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nNume: ");
        String nume=scanner.nextLine();
        System.out.println("\nPrenume: ");
        String prenume=scanner.nextLine();
        System.out.println("\nLuna: ");
        int luna= Integer.parseInt(scanner.nextLine());
        ArrayList<String> list= (ArrayList<String>) service.PrieteniLuna(prenume,nume,luna);
        System.out.print("Prenume | Nume | Data\n");
        int count = 0;
        for(String i:list){
            System.out.print(i);
            count++;
            if(count == 3){
                System.out.print("\n");
                count = 0;
            }
            else
                System.out.print(" | ");
        }
    }

}
