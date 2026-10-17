// Low-Level Design for a Craigslist-like System in Java

// ===== DOMAIN MODELS =====

/**
 * Represents a user in the system
 */
class User {
    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private String location;
    private Date registrationDate;
    private boolean isVerified;
    private List<Listing> listings;
    private List<Message> messages;
    
    // Getters and setters
    // Authentication methods
    public boolean authenticate(String password) {
        // Hash password and compare with stored hash
        return PasswordUtils.verify(password, this.passwordHash);
    }
    
    public void createListing(Listing listing) {
        // Add listing to user's listings
        listings.add(listing);
    }
}

/**
 * Represents a listing/ad posted on the platform
 */
class Listing {
    private String listingId;
    private String title;
    private String description;
    private BigDecimal price;
    private User owner;
    private Category category;
    private SubCategory subCategory;
    private Location location;
    private Date postDate;
    private Date expiryDate;
    private ListingStatus status;
    private List<Image> images;
    private List<Flag> flags;
    private int viewCount;
    
    // Getters and setters
    
    public void updateStatus(ListingStatus newStatus) {
        this.status = newStatus;
        // Trigger notification if necessary
    }
    
    public void renew() {
        this.expiryDate = DateUtils.addDays(new Date(), 30);
        // Update in database
    }
}

/**
 * Enum representing possible status values for a listing
 */
enum ListingStatus {
    ACTIVE,
    EXPIRED,
    DELETED,
    FLAGGED,
    SOLD
}

/**
 * Represents a category for listings
 */
class Category {
    private String categoryId;
    private String name;
    private String description;
    private List<SubCategory> subCategories;
    // Getters and setters
}

/**
 * Represents a subcategory for more specific listing classification
 */
class SubCategory {
    private String subCategoryId;
    private String name;
    private Category parentCategory;
    
    // Getters and setters
}

/**
 * Represents a geographic location
 */
class Location {
    private String city;
    private String state;
    private String zipCode;
    private double latitude;
    private double longitude;
    
    // Getters and setters
    
    public double distanceFrom(Location other) {
        // Calculate distance using latitude and longitude
        return GeoUtils.calculateDistance(this.latitude, this.longitude, 
                                         other.latitude, other.longitude);
    }
}

/**
 * Represents an image attached to a listing
 */
class Image {
    private String imageId;
    private String url;
    private String thumbnailUrl;
    private Listing listing;
    
    // Getters and setters
}

/**
 * Represents a flag raised by users for inappropriate content
 */
class Flag {
    private String flagId;
    private User reporter;
    private Listing listing;
    private String reason;
    private FlagStatus status;
    private Date reportDate;
    
    // Getters and setters
}

/**
 * Enum representing possible status values for a flag
 */
enum FlagStatus {
    PENDING,
    REVIEWED,
    RESOLVED,
    DISMISSED
}

/**
 * Represents a message between users
 */
class Message {
    private String messageId;
    private User sender;
    private User recipient;
    private String content;
    private Date timestamp;
    private boolean isRead;
    private Listing relatedListing;
    
    // Getters and setters
    
    public void markAsRead() {
        this.isRead = true;
        // Update in database
    }
}

// ===== REPOSITORIES =====

/**
 * Repository interface for User persistence
 */
interface UserRepository {
    User findById(String userId);
    User findByEmail(String email);
    User findByUsername(String username);
    List<User> findAll();
    void save(User user);
    void delete(User user);
}

/**
 * Repository interface for Listing persistence
 */
interface ListingRepository {
    Listing findById(String listingId);
    List<Listing> findByCategory(Category category);
    List<Listing> findBySubCategory(SubCategory subCategory);
    List<Listing> findByLocation(Location location, double radiusInMiles);
    List<Listing> findByOwner(User owner);
    List<Listing> search(String keyword, Category category, Location location, double maxPrice);
    List<Listing> findRecentListings(int limit);
    void save(Listing listing);
    void delete(Listing listing);
}

/**
 * Repository interface for Message persistence
 */
interface MessageRepository {
    Message findById(String messageId);
    List<Message> findBySender(User sender);
    List<Message> findByRecipient(User recipient);
    List<Message> findConversation(User user1, User user2);
    void save(Message message);
    void delete(Message message);
}

/**
 * Repository interface for Flag persistence
 */
interface FlagRepository {
    Flag findById(String flagId);
    List<Flag> findByStatus(FlagStatus status);
    List<Flag> findByListing(Listing listing);
    void save(Flag flag);
}

// ===== SERVICES =====

/**
 * Service for user-related operations
 */
class UserService {
    private UserRepository userRepository;
    private EmailService emailService;
    
    // Constructor with dependency injection
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    public User registerUser(String username, String email, String password, String phoneNumber) {
        // Validate input
        validateUserInput(username, email, password);
        
        // Check if user already exists
        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        
        // Create new user
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtils.hash(password));
        user.setPhoneNumber(phoneNumber);
        user.setRegistrationDate(new Date());
        user.setVerified(false);
        
        // Save user
        userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(user);
        
        return user;
    }
    
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.authenticate(password)) {
            return user;
        }
        return null;
    }
    
    // Other user-related methods
    private void validateUserInput(String username, String email, String password) {
        // Validation logic
    }
}

/**
 * Service for listing-related operations
 */
class ListingService {
    private ListingRepository listingRepository;
    private UserRepository userRepository;
    private NotificationService notificationService;
    
    // Constructor with dependency injection
    public ListingService(ListingRepository listingRepository, 
                         UserRepository userRepository,
                         NotificationService notificationService) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }
    
    public Listing createListing(String userId, String title, String description, 
                                BigDecimal price, String categoryId, String subCategoryId,
                                Location location, List<Image> images) {
        // Validate input
        validateListingInput(title, description, price);
        
        // Get user
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        
        // Create listing
        Listing listing = new Listing();
        listing.setListingId(UUID.randomUUID().toString());
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setPrice(price);
        listing.setOwner(user);
        // Set category and subcategory
        listing.setLocation(location);
        listing.setPostDate(new Date());
        listing.setExpiryDate(DateUtils.addDays(new Date(), 30));
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setImages(images);
        listing.setViewCount(0);
        
        // Save listing
        listingRepository.save(listing);
        
        // Update user's listings
        user.createListing(listing);
        userRepository.save(user);
        
        return listing;
    }
    
    public List<Listing> searchListings(String keyword, String categoryId, 
                                      Location location, double radius, double maxPrice) {
        // Implementation of search functionality
        return listingRepository.search(keyword, getCategoryById(categoryId), location, maxPrice);
    }
    
    public void flagListing(String listingId, String userId, String reason) {
        Listing listing = listingRepository.findById(listingId);
        User reporter = userRepository.findById(userId);
        
        if (listing == null || reporter == null) {
            throw new NotFoundException("Listing or user not found");
        }
        
        Flag flag = new Flag();
        flag.setFlagId(UUID.randomUUID().toString());
        flag.setListing(listing);
        flag.setReporter(reporter);
        flag.setReason(reason);
        flag.setStatus(FlagStatus.PENDING);
        flag.setReportDate(new Date());
        
        // Save flag and notify moderators
        // ...
    }
    
    // Other listing-related methods
}

/**
 * Service for messaging between users
 */
class MessageService {
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private ListingRepository listingRepository;
    private NotificationService notificationService;
    
    // Constructor with dependency injection
    public MessageService(MessageRepository messageRepository, 
                         UserRepository userRepository,
                         ListingRepository listingRepository,
                         NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.notificationService = notificationService;
    }
    
    public Message sendMessage(String senderId, String recipientId, String content, String listingId) {
        User sender = userRepository.findById(senderId);
        User recipient = userRepository.findById(recipientId);
        Listing listing = null;
        
        if (listingId != null) {
            listing = listingRepository.findById(listingId);
        }
        
        if (sender == null || recipient == null) {
            throw new UserNotFoundException("User not found");
        }
        
        Message message = new Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);
        message.setTimestamp(new Date());
        message.setRead(false);
        message.setRelatedListing(listing);
        
        // Save message
        messageRepository.save(message);
        
        // Notify recipient
        notificationService.notifyNewMessage(recipient, message);
        
        return message;
    }
    
    public List<Message> getConversation(String userId1, String userId2) {
        User user1 = userRepository.findById(userId1);
        User user2 = userRepository.findById(userId2);
        
        if (user1 == null || user2 == null) {
            throw new UserNotFoundException("User not found");
        }
        
        return messageRepository.findConversation(user1, user2);
    }
    
    // Other messaging-related methods
}

/**
 * Service for notification delivery
 */
class NotificationService {
    private EmailService emailService;
    private SMSService smsService;
    
    // Constructor with dependency injection
    public NotificationService(EmailService emailService, SMSService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }
    
    public void notifyNewMessage(User recipient, Message message) {
        // Send email notification
        emailService.sendMessageNotification(recipient, message);
        
        // Send SMS notification if user has opted in
        if (recipient.isOptedInForSMS()) {
            smsService.sendMessageNotification(recipient, message);
        }
    }
    
    // Other notification methods
}

/**
 * Service for email operations
 */
class EmailService {
    public void sendVerificationEmail(User user) {
        // Implementation to send verification email
    }
    
    public void sendMessageNotification(User recipient, Message message) {
        // Implementation to send message notification email
    }
    
    // Other email methods
}

/**
 * Service for SMS operations
 */
class SMSService {
    public void sendMessageNotification(User recipient, Message message) {
        // Implementation to send message notification SMS
    }
    
    // Other SMS methods
}

// ===== CONTROLLERS =====

/**
 * REST controller for user-related endpoints
 */
@RestController
@RequestMapping("/api/users")
class UserController {
    private UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(request.getUsername(), request.getEmail(),
                                           request.getPassword(), request.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(user));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getEmail(), request.getPassword());
        if (user != null) {
            String token = JwtUtils.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    // Other user endpoints
    
    private UserDTO convertToDTO(User user) {
        // Convert User to UserDTO
        return new UserDTO(user.getUserId(), user.getUsername(), user.getEmail());
    }
}

/**
 * REST controller for listing-related endpoints
 */
@RestController
@RequestMapping("/api/listings")
class ListingController {
    private ListingService listingService;
    
    @Autowired
    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }
    
    @PostMapping
    public ResponseEntity<ListingDTO> createListing(@RequestBody ListingRequest request,
                                                 @RequestHeader("Authorization") String token) {
        String userId = JwtUtils.getUserIdFromToken(token);
        Listing listing = listingService.createListing(userId, request.getTitle(),
                                                     request.getDescription(), request.getPrice(),
                                                     request.getCategoryId(), request.getSubCategoryId(),
                                                     request.getLocation(), request.getImages());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(listing));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ListingDTO>> searchListings(@RequestParam String keyword,
                                                      @RequestParam(required = false) String categoryId,
                                                      @RequestParam(required = false) Double latitude,
                                                      @RequestParam(required = false) Double longitude,
                                                      @RequestParam(required = false) Double radius,
                                                      @RequestParam(required = false) Double maxPrice) {
        Location location = null;
        if (latitude != null && longitude != null) {
            location = new Location();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }
        
        List<Listing> listings = listingService.searchListings(keyword, categoryId, location, 
                                                             radius != null ? radius : 10.0,
                                                             maxPrice != null ? maxPrice : Double.MAX_VALUE);
        return ResponseEntity.ok(listings.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }
    
    // Other listing endpoints
    
    private ListingDTO convertToDTO(Listing listing) {
        // Convert Listing to ListingDTO
        return new ListingDTO(listing.getListingId(), listing.getTitle(), 
                            listing.getDescription(), listing.getPrice().toString(),
                            listing.getOwner().getUsername(), listing.getPostDate());
    }
}

/**
 * REST controller for message-related endpoints
 */
@RestController
@RequestMapping("/api/messages")
class MessageController {
    private MessageService messageService;
    
    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageRequest request,
                                               @RequestHeader("Authorization") String token) {
        String senderId = JwtUtils.getUserIdFromToken(token);
        Message message = messageService.sendMessage(senderId, request.getRecipientId(),
                                                   request.getContent(), request.getListingId());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(message));
    }
    
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<MessageDTO>> getConversation(@PathVariable String userId,
                                                         @RequestHeader("Authorization") String token) {
        String currentUserId = JwtUtils.getUserIdFromToken(token);
        List<Message> messages = messageService.getConversation(currentUserId, userId);
        return ResponseEntity.ok(messages.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }
    
    // Other message endpoints
    
    private MessageDTO convertToDTO(Message message) {
        // Convert Message to MessageDTO
        return new MessageDTO(message.getMessageId(), message.getSender().getUserId(),
                            message.getRecipient().getUserId(), message.getContent(),
                            message.getTimestamp(), message.isRead());
    }
}

// ===== DATA TRANSFER OBJECTS =====

/**
 * DTO for User registration requests
 */
class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    
    // Getters and setters
}

/**
 * DTO for login requests
 */
class LoginRequest {
    private String email;
    private String password;
    
    // Getters and setters
}

/**
 * DTO for authentication responses
 */
class AuthResponse {
    private String token;
    
    public AuthResponse(String token) {
        this.token = token;
    }
    
    // Getters
}

/**
 * DTO for User data to be sent to clients
 */
class UserDTO {
    private String userId;
    private String username;
    private String email;
    
    public UserDTO(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
    
    // Getters
}

/**
 * DTO for Listing creation requests
 */
class ListingRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private String categoryId;
    private String subCategoryId;
    private Location location;
    private List<Image> images;
    
    // Getters and setters
}

/**
 * DTO for Listing data to be sent to clients
 */
class ListingDTO {
    private String id;
    private String title;
    private String description;
    private String price;
    private String ownerName;
    private Date postDate;
    
    public ListingDTO(String id, String title, String description, String price, 
                     String ownerName, Date postDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.ownerName = ownerName;
        this.postDate = postDate;
    }
    
    // Getters
}

/**
 * DTO for Message creation requests
 */
class MessageRequest {
    private String recipientId;
    private String content;
    private String listingId;
    
    // Getters and setters
}

/**
 * DTO for Message data to be sent to clients
 */
class MessageDTO {
    private String id;
    private String senderId;
    private String recipientId;
    private String content;
    private Date timestamp;
    private boolean isRead;
    
    public MessageDTO(String id, String senderId, String recipientId, String content,
                    Date timestamp, boolean isRead) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }
    
    // Getters
}

// ===== EXCEPTIONS =====

/**
 * Exception for when a user is not found
 */
class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

/**
 * Exception for when a user already exists
 */
class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

/**
 * Exception for when a resource is not found
 */
class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

// ===== UTILITY CLASSES =====

/**
 * Utility class for password hashing and verification
 */
class PasswordUtils {
    public static String hash(String password) {
        // Implementation of password hashing (e.g., BCrypt)
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public static boolean verify(String password, String hash) {
        // Implementation of password verification
        return BCrypt.checkpw(password, hash);
    }
}

/**
 * Utility class for JWT token generation and validation
 */
class JwtUtils {
    private static final String SECRET_KEY = "your-secret-key";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    
    public static String generateToken(User user) {
        // Implementation of JWT token generation
        return Jwts.builder()
                .setSubject(user.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    
    public static String getUserIdFromToken(String token) {
        // Extract userId from token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

/**
 * Utility class for date operations
 */
class DateUtils {
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
}

/**
 * Utility class for geographic calculations
 */
class GeoUtils {
    private static final double EARTH_RADIUS = 6371; // kilometers
    
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Implementation of Haversine formula
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
}

// ===== DATABASE IMPLEMENTATION =====

/**
 * Implementation of UserRepository using JPA
 */
@Repository
class JpaUserRepository implements UserRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public User findById(String userId) {
        return entityManager.find(User.class, userId);
    }
    
    @Override
    public User findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    // Other method implementations
}

/**
 * Implementation of ListingRepository using JPA
 */
@Repository
class JpaListingRepository implements ListingRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Listing findById(String listingId) {
        return entityManager.find(Listing.class, listingId);
    }
    
    @Override
    public List<Listing> findByCategory(Category category) {
        TypedQuery<Listing> query = entityManager.createQuery(
            "SELECT l FROM Listing l WHERE l.category = :category AND l.status = :status", 
            Listing.class);
        query.setParameter("category", category);
        query.setParameter("status", ListingStatus.ACTIVE);
        
        return query.getResultList();
    }
    
    // Other method implementations
}

// ===== CONFIGURATION =====

/**
 * Configuration class for Spring Security
 */
@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/users/register", "/api/users/login").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

/**
 * JWT Authentication Filter for validating tokens
 */
@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractJwtFromRequest(request);
            if (token != null) {
                String userId = JwtUtils.getUserIdFromToken(token);
                // Set up authentication in Spring Security context
                // ...
            }
        } catch (Exception e) {
            // Handle exception
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}