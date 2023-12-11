package com.example.laboratorjavafx.service;

import com.example.laboratorjavafx.domain.Entity;
import com.example.laboratorjavafx.domain.FriendRequest;
import com.example.laboratorjavafx.domain.FriendShip;
import com.example.laboratorjavafx.domain.User;
import com.example.laboratorjavafx.domain.validators.ValidationException;
import com.example.laboratorjavafx.domain.validators.Validator;
import com.example.laboratorjavafx.repository.Repository;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.example.laboratorjavafx.domain.FriendshipStatus.*;

public class Service implements ServiceInterface<UUID> {

    private final Repository userRepo;
    private final Validator userValidator;
    private final Repository friendshipRepo;
    private final Repository friendRequestRepo;
    private final Validator friendshipValidator;

    /**
     * Constructor
     *
     * @param userRepo            - the repository for the users
     * @param userValidator       - the validator for the users
     * @param friendshipRepo      - the repository for the friendships
     * @param friendRequestRepo
     * @param friendshipValidator - the validator for the friendships
     */
    public Service(Repository userRepo, Validator userValidator, Repository friendshipRepo, Repository friendRequestRepo, Validator friendshipValidator) {
        this.userRepo = userRepo;
        this.userValidator = userValidator;
        this.friendshipRepo = friendshipRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.friendshipValidator = friendshipValidator;
    }

    public Service(Repository userRepo, Validator userValidator, Repository friendshipRepo, Validator friendshipValidator) {
        this.userRepo = userRepo;
        this.userValidator = userValidator;
        this.friendshipRepo = friendshipRepo;
        this.friendRequestRepo = null;
        this.friendshipValidator = friendshipValidator;
    }

    /**
     * Adds an user
     * @param user - the user we want to add
     * @return true if the user was added, false otherwise
     *
     * We use this method to add an user to the repository.
     * We first check if the user is not null and if the email is not null and if the user doesn't already exist.
     * Then we check if the user is valid and if it is, we add it to the repository.
     * If the user already exists, we throw an exception.
     */
    @Override
    public boolean addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null!");
        }
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Email must not be null!");
        }

        // Verifică dacă utilizatorul cu același email există deja
        User existingUser = getUserByEmail(user.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Email already exists!");
        }

        try {
            this.userValidator.validate(user);
            Optional<Entity<UUID>> u = userRepo.save(user);
            if (u.isPresent()) {
                throw new IllegalArgumentException("A user with this ID already exists!");
            }
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            return false;
        }

        return true;
    }



    /**
     * Deletes an user
     * @param email - the email of the user we want to delete
     * @return Entity<UUID> - the deleted user
     *
     * We use this method to delete an user from the repository.
     * We first check if the email is not null and if the user exists.
     * Then we delete the user and their friendships.
     * If the user doesn't exist, we throw an exception.
     */
    @Override
    public Entity<UUID> deleteUser(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email must not be null!");
        }

        // Verifică dacă utilizatorul cu acest email există
        User user = getUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email doesn't exist!");
        }


        // Șterge prieteniile utilizatorului
        deleteFriendshipsForUser(user);

        // Șterge utilizatorul și returnează entitatea ștearsă, folosind get() pentru a obține valoarea din Optional
        Optional<Entity<UUID>> deletedUserOptional = userRepo.delete(user.getId());

        if (deletedUserOptional.isPresent()) {
            return deletedUserOptional.get();
        } else {
            // Dacă Optional este gol, aruncăm o excepție sau returnăm null, în funcție de cerințe
            throw new IllegalStateException("User deletion failed.");
        }
    }

    /**
     * Deletes friendships for a user
     * @param user - the user for which to delete friendships
     */
    private void deleteFriendshipsForUser(User user) {
        List<FriendShip> friendshipsToRemove = new ArrayList<>();
        Iterable<FriendShip> friendships = friendshipRepo.findAll();

        for (FriendShip friendship : friendships) {
            if (friendship.getUser1().equals(user) || friendship.getUser2().equals(user)) {
                // Adaugă prietenia la lista de prietenii de șters
                friendshipsToRemove.add(friendship);
            }
        }

        // Șterge toate prieteniile din lista
        for (FriendShip friendship : friendshipsToRemove) {
            friendshipRepo.delete(friendship.getId());
        }
    }



    /**
     * Creates a friendship between two users
     * @param email1 and
     * @param email2 - the emails of the user we have to create a friendship between
     *
     * @return true if the friendship was created, false otherwise
     */
    @Override
    public boolean createFriendship(String email1, String email2) {
        if (email1 == null || email2 == null) {
            throw new IllegalArgumentException("Emails must not be null!");
        }

        User u1 = getUserByEmail(email1);
        User u2 = getUserByEmail(email2);

        if (u1 == null || u2 == null || u1.equals(u2)) {
            throw new IllegalArgumentException("Users must not be null and must be different!");
        }

        var u1Friends = u1.getFriends();
        var u2Friends = u2.getFriends();

        if (u1Friends.contains(u2) || u2Friends.contains(u1)) {
            throw new IllegalArgumentException("Friendship already exists!");
        }

        var friendship = new FriendShip(u1, u2, ACCEPTED);

        try {
            this.friendshipValidator.validate(friendship);
            Optional<Entity<UUID>> f = friendshipRepo.save(friendship);

            if (f.isPresent()) {
                throw new IllegalArgumentException("These users are already friends!");
            }

            u1.addFriend(u2);
            u2.addFriend(u1);

        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage()); // Accesează mesajul de eroare folosind getMessage()
            return false;
        }

        return true;
    }

    /**
     * Deletes a friendship
     * @param email1 - the email of the first user
     * @param email2 - the email of the second user
     * @return Entity<UUID> - the deleted friendship
     *
     * We use this method to delete a friendship between two users.
     * We first check if the emails are not null and if the users are not null and different.
     * Then we check if the friendship exists and if it does, we delete it.
     * If the friendship doesn't exist, we throw an exception.
     */
    @Override
    public Entity<UUID> deleteFriendship(String email1, String email2) {
        User u1, u2;

        if (email1 == null || email2 == null) {
            throw new IllegalArgumentException("Emails must not be null!");
        }
        u1 = getUserByEmail(email1);
        u2 = getUserByEmail(email2);

        if (u1 == null || u2 == null || u1.equals(u2)) {
            throw new ValidationException("Users must be not null and different!");
        }

        Iterable<FriendShip> l = friendshipRepo.findAll();
        for (FriendShip el : l) {
            if ((el.getUser1().getId().equals(u1.getId()) && el.getUser2().getId().equals(u2.getId())) ||
                    (el.getUser1().getId().equals(u2.getId()) && el.getUser2().getId().equals(u1.getId()))) {
                Optional<Entity<UUID>> f = friendshipRepo.delete(el.getId());

                if (f.isPresent()) {
                    return f.get();
                }

                break;
            }
        }

        throw new IllegalArgumentException("Friendship not found!");
    }


    /**
     * Returns all the users
     * @return Iterable<User> - all the users
     */
    @Override
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }
    /**
     * Returns all the friendships
     * @return Iterable<FriendShip> - all the friendships
     */
    @Override
    public Iterable<FriendShip> getAllFriendships() {
        return friendshipRepo.findAll();
    }

    @Override
    public Iterable<FriendRequest> getAllFriendrequests() {
        return friendRequestRepo.findAll();
    }

    /**
     * Adds some predefined values to the repository
     * We use this method to add some predefined values to the repository
     */
    @Override
    public void add_Predefined_Values() {
        User u1 = new User("Paul", "Caba", "cabapaul@yahoo.com", "1234");
        User u2 = new User("Mircea", "Maior", "maiormircea@yahoo.com", "123");
        User u3 = new User("Raul", "Vida", "vidaraul@yahoo.com", "12345");
        User u4 = new User("Andrei", "Balan", "balanandrei@yahoo.com", "123456");
        User u5 = new User("Gheorghe", "Groze", "grozegheorghe@yahoo.com", "1234567");
        User u6 = new User("Cristian", "Popescu", "popescucristian@yahoo.com", "12345678");
        User u7 = new User("Mirel", "Bala", "balamirel@yahoo.com", "123456789");
                this.addUser(u1);
                this.addUser(u2);
                this.addUser(u3);
                this.addUser(u4);
                this.addUser(u5);
                this.addUser(u6);
                this.addUser(u7);
                this.createFriendship(u1.getEmail(), u2.getEmail());
                this.createFriendship(u1.getEmail(), u7.getEmail());
                this.createFriendship(u6.getEmail(), u5.getEmail());
                this.createFriendship(u5.getEmail(), u4.getEmail());
                this.createFriendship(u4.getEmail(), u3.getEmail());
    }

    /**
     * Returns the number of communities
     * @return int - the number of communities
     *
     * We use a DFS for each node to count the number of conex elements.
     */
    @Override
    public int numberOfCommunities() {
        Iterable<User> it = userRepo.findAll();
        Set<User> set = new HashSet<>();
        int count = 0;

        for(User u: it){
            if(!set.contains(u)){
                ++count;
                DFS(u, set);
            }
        }
        return count;
    }

    /**
     * a simple DFS algorithm
     *
     * @param u - the current user
     * @param set - the set of visited users
     *
     * @return List<User> - the list of users in the current community
     */
    private List<User> DFS(User u, Set<User> set) {
        List<User> list = new ArrayList<>();
        list.add(u);
        set.add(u);

        u.getFriends().forEach(f -> {
            if (!set.contains(f)) {
                List<User> l = DFS(f, set);
                l.forEach(x -> list.add(x));
            }
        });

        return list;
    }

    /**
     * Returns the most sociable community
     *
     * @return list - the list of users in the most sociable community
     *
     *
     * We use a BFS to find the longest path in a community.
     */
    @Override
    public Iterable<Iterable<User>> mostSociableCommunity() {
        List<Iterable<User>> list = new ArrayList<>();
        Iterable<User> it = userRepo.findAll();
        Set<User> set = new HashSet<>();

        int max = -1;

        for (User u : it) {
            if (!set.contains(u)) {
                List<User> aux = DFS(u, set);
                int l = longestPath(aux);
                if (l > max) {
                    list = new ArrayList<>();
                    list.add(aux);
                    max = l;
                } else if (l == max) {
                    list.add(aux);
                }
            }
        }

        return list;
    }


    /**
     * Returns the longest path in a community
     *
     * @param nodes - the list of users in the community
     *
     * @return int - the longest path in the community
     *
     * We use a BFS to find the longest path in a community.
     */
    private int longestPath(List<User> nodes){
        int max = 0;
        for(User u : nodes) {
            int l = longestPathFromSource(u);
            if(max < l)
                max = l;
        }
        return max;
    }

    /**
     * Returns the longest path from a source
     *
     * @param source - the source user
     *
     * @return int - the longest path from the source
     *
     * We use a BFS to find the longest path from a source.
     */
    private int longestPathFromSource(User source){
        Set<User> set = new HashSet<>();
        return BFS(source, set);
    }

    /**
     * a simple BFS algorithm
     *
     * @param source - the current user
     * @param set - the set of visited users
     *
     * @return int - the longest path from the current user
     */
    private int BFS(User source, Set<User> set){
        int max = -1;
        for(User f : source.getFriends())
            if(!set.contains(f)) {
                set.add(f);
                int l = BFS(f, set);
                if(l > max)
                    max = l;
                set.remove(f);
            }

        return max + 1;
    }


    @Override
    public List<List<User>> getAllCommunities() {
        Iterable<User> it = userRepo.findAll();
        Set<User> set = new HashSet<>();
        List<List<User>> l = new ArrayList<>();

        it.forEach(u -> {
            if (!set.contains(u)) {
                l.add(DFS(u, set));
            }
        });

        return l;
    }
    @Override
    public User getUserByEmail(String email) {
        Iterable<User> it = userRepo.findAll();

        User[] foundUser = new User[1]; // Folosim un array cu o singură poziție pentru a stoca utilizatorul găsit

        it.forEach(u -> {
            if (u.getEmail().equals(email)) {
                foundUser[0] = u;
            }
        });

        return foundUser[0];
    }


    @Override
    public void acceptFriendship(String email1, String email2) {
        deleteFriendship(email1, email2);
        createFriendship(email1, email2);
    }

    /**
     * Declines a friend request
     * @param email1 - the email of the first user
     * @param email2 - the email of the second user
     */
    @Override
    public void declineFriendRequest(String email1, String email2) {
        User u1, u2;

        if (email1 == null || email2 == null) {
            throw new IllegalArgumentException("Emails must not be null!");
        }

        u1 = getUserByEmail(email1);
        u2 = getUserByEmail(email2);

        if (u1 == null || u2 == null || u1.equals(u2)) {
            throw new ValidationException("Users must be not null and different!");
        }

        FriendRequest f = findFriendRequestBetweenUsers(u1, u2).orElse(null);
        f.setStatus(REJECTED);

        friendRequestRepo.update(f);

    }

    /**
     * Accepts a friend request
     * @param email1 - the email of the first user
     * @param email2 - the email of the second user
     */
    @Override
    public void acceptFriendRequest(String email1, String email2) {
        User u1, u2;

        if (email1 == null || email2 == null) {
            throw new IllegalArgumentException("Emails must not be null!");
        }

        u1 = getUserByEmail(email1);
        u2 = getUserByEmail(email2);



        if (u1 == null || u2 == null || u1.equals(u2)) {
            throw new ValidationException("Users must be not null and different!");
        }

        FriendRequest f = findFriendRequestBetweenUsers(u1, u2).orElse(null);
        f.setStatus(ACCEPTED);

        friendRequestRepo.update(f);
        friendshipRepo.save(new FriendShip(u1, u2, ACCEPTED));
    }


    /**
     * Returns FriendRequest between two users
     * @return Optional<FriendRequest> - the friend request between the two users
     */
    private Optional<FriendRequest> findFriendRequestBetweenUsers(User u1, User u2) {
        Iterable<FriendRequest> allRequests = friendRequestRepo.findAll();

        for (FriendRequest request : allRequests) {
            if ((request.getFromUser().equals(u1) && request.getToUser().equals(u2)) ||
                    (request.getFromUser().equals(u2) && request.getToUser().equals(u1))) {
                return Optional.of(request);
            }
        }

        return Optional.empty();
    }



    /**
     * Creates a friend request
     * @param email1 - the email of the first user
     * @param email2 - the email of the second user
     */
    @Override
    public void createFriendRequest(String email1, String email2) {
        User u1 = getUserByEmail(email1);
        User u2 = getUserByEmail(email2);

        if (u1 == null || u2 == null || u1.equals(u2)) {
            throw new IllegalArgumentException("Users must not be null and must be different!");
        }

        Iterable<FriendRequest> existingRequests = friendRequestRepo.findAll();
        for (FriendRequest existingRequest : existingRequests) {
            if (((existingRequest.getFromUser().equals(u1) && existingRequest.getToUser().equals(u2))
                    || (existingRequest.getFromUser().equals(u2) && existingRequest.getToUser().equals(u1)) && existingRequest.getStatus() == PENDING)) {
                System.out.print("This friend request already exists!\n");
                return;
            }
        }

        Iterable<FriendShip> friendships = getAllFriendships();

        for (FriendShip friendship : friendships) {
            if ((friendship.getUser1().equals(u1) && friendship.getUser2().equals(u2)) ||
                    (friendship.getUser1().equals(u2) && friendship.getUser2().equals(u1))) {
                System.out.print("Users are already friends!\n");
                return;
            }
        }

        var friendRequest = new FriendRequest(u1, u2, PENDING);

        friendRequestRepo.save(friendRequest);
    }


    public List<String> PrieteniLuna(String p, String n, Integer month) {
        ArrayList<String> users = new ArrayList<>();

        Iterable<FriendShip> prt = friendshipRepo.findAll();
        User u = getUserByNumePrenume(p, n);
        for (FriendShip prtt : prt) {
            if (prtt.getUser1().equals(u) && prtt.getFriendsFrom().getMonth().getValue() == month) {
                users.add(prtt.getUser2().getFirstName());
                users.add(prtt.getUser2().getLastName());
                users.add(prtt.getFriendsFrom().toString()); // Adăugarea datelor friendsFrom în lista de șiruri
            }
            if (prtt.getUser2().equals(u) && prtt.getFriendsFrom().getMonth().getValue() == month) {
                users.add(prtt.getUser1().getFirstName());
                users.add(prtt.getUser1().getLastName());
                users.add(prtt.getFriendsFrom().toString()); // Adăugarea datelor friendsFrom în lista de șiruri
            }
        }

        return users;
    }

    public User getUserByNumePrenume(String nume, String prenume) {
        return printUsers().stream()
                .filter(u -> u.getFirstName().equals(nume) && u.getLastName().equals(prenume))
                .findFirst()
                .orElse(null);
    }

    public List<User> printUsers() {
        Iterable<User> usersIterable = userRepo.findAll();
        List<User> lista = new ArrayList<>();

        usersIterable.forEach(lista::add);
        return lista;
    }



    public int getFriendshipCount() {
        return friendshipRepo.size();
    }

    public int getUserCount() {
        return userRepo.size();
    }

    public void updateUser(UUID id, String newFirstName, String newLastName, String newemail) {
        User user = (User) userRepo.findOne(id).get();
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setEmail(newemail);
        userRepo.update(user);
        updateFriendshipsForUser(user);
    }

    private void updateFriendshipsForUser(User user) {
        Iterable<FriendShip> friendships = friendshipRepo.findAll();

        for (FriendShip friendship : friendships) {
            if (friendship.getUser1().equals(user)) {
                friendship.setUser1(user);
                friendshipRepo.update(friendship);
            }
            if (friendship.getUser2().equals(user)) {
                friendship.setUser2(user);
                friendshipRepo.update(friendship);
            }
        }
    }

    public Color getColor() {
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\MOP\\LABORATOR8\\src\\main\\java\\com\\example\\laboratorjavafx\\color.txt"))) {
            String linie = br.readLine();
            return Color.valueOf(linie);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return Color.WHITE;
    }
}
