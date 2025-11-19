import java.util.*;

class DuplicateIdException extends Exception {
    public DuplicateIdException(String msg) { super(msg); }
}

class InvalidPriceException extends Exception {
    public InvalidPriceException(String msg) { super(msg); }
}

class NonRefundableException extends Exception {
    public NonRefundableException(String msg) { super(msg); }
}

class NotFoundException extends Exception {
    public NotFoundException(String msg) { super(msg); }
}

abstract class Product {
    protected String id;
    protected String name;
    protected double price;

    public Product(String id, String name, double price) throws InvalidPriceException {
        if (price < 0) throw new InvalidPriceException("Giá sản phẩm không hợp lệ: " + price);
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() { return id; }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "', price=" + price + "}";
    }
}

interface Deliverable {
    void deliver();
}

interface Refundable {
    void refund() throws NonRefundableException;
}

class Book extends Product implements Deliverable, Refundable {
    private String author;

    public Book(String id, String name, double price, String author) throws InvalidPriceException {
        super(id, name, price);
        this.author = author;
    }

    @Override
    public void deliver() {
        System.out.println("Giao sách: " + name + " của tác giả " + author);
    }

    @Override
    public void refund() {
        System.out.println("Hoàn tiền sách: " + name);
    }

    @Override
    public String toString() {
        return "Book{id='" + id + "', name='" + name + "', price=" + price + ", author='" + author + "'}";
    }
}

class Phone extends Product implements Deliverable, Refundable {
    private String brand;

    public Phone(String id, String name, double price, String brand) throws InvalidPriceException {
        super(id, name, price);
        this.brand = brand;
    }

    @Override
    public void deliver() {
        System.out.println("Giao điện thoại: " + name + ", hãng: " + brand);
    }

    @Override
    public void refund() {
        System.out.println("Hoàn tiền điện thoại: " + name);
    }

    @Override
    public String toString() {
        return "Phone{id='" + id + "', name='" + name + "', price=" + price + ", brand='" + brand + "'}";
    }
}

class Laptop extends Product implements Deliverable, Refundable {
    private String brand;

    public Laptop(String id, String name, double price, String brand) throws InvalidPriceException {
        super(id, name, price);
        this.brand = brand;
    }

    @Override
    public void deliver() {
        System.out.println("Giao laptop: " + name + ", hãng: " + brand);
    }

    @Override
    public void refund() throws NonRefundableException {
        throw new NonRefundableException("Laptop không hỗ trợ hoàn tiền: " + name);
    }

    @Override
    public String toString() {
        return "Laptop{id='" + id + "', name='" + name + "', price=" + price + ", brand='" + brand + "'}";
    }
}

class Customer {
    String id;
    String name;

    public Customer(String id, String name) { this.id = id; this.name = name; }

    @Override
    public String toString() {
        return "Customer{id='" + id + "', name='" + name + "'}";
    }
}

class Order {
    String id;
    Customer customer;
    List<Product> items = new ArrayList<>();

    public Order(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    public void addProduct(Product p) { items.add(p); }

    public double total() {
        double t = 0;
        for (Product p : items) t += p.price;
        return t;
    }
}

interface Payment {
    void pay(Order order);
}

class CreditCardPayment implements Payment {
    @Override
    public void pay(Order order) {
        System.out.println("Thanh toán bằng Credit Card: " + order.total());
    }
}

class PaypalPayment implements Payment {
    @Override
    public void pay(Order order) {
        System.out.println("Thanh toán bằng PayPal: " + order.total());
    }
}

class CashPayment implements Payment {
    @Override
    public void pay(Order order) {
        System.out.println("Thanh toán tiền mặt: " + order.total());
    }
}

class MoMoPayment implements Payment {
    @Override
    public void pay(Order order) {
        System.out.println("Thanh toán MoMo: " + order.total());
    }
}

interface Repository<T> {
    void add(T item) throws DuplicateIdException;
    void update(T item) throws NotFoundException;
    void delete(String id) throws NotFoundException;
    List<T> findAll();
}

abstract class BaseRepository<T> implements Repository<T> {
    protected Map<String, T> storage = new HashMap<>();

    protected abstract String getId(T item);

    @Override
    public void add(T item) throws DuplicateIdException {
        if (storage.containsKey(getId(item)))
            throw new DuplicateIdException("ID đã tồn tại: " + getId(item));
        storage.put(getId(item), item);
    }

    @Override
    public void update(T item) throws NotFoundException {
        if (!storage.containsKey(getId(item)))
            throw new NotFoundException("Không tìm thấy ID: " + getId(item));
        storage.put(getId(item), item);
    }

    @Override
    public void delete(String id) throws NotFoundException {
        if (!storage.containsKey(id)) throw new NotFoundException("Không tìm thấy ID: " + id);
        storage.remove(id);
    }

    @Override
    public List<T> findAll() { return new ArrayList<>(storage.values()); }
}

class ProductRepository extends BaseRepository<Product> {
    @Override
    protected String getId(Product item) { return item.getId(); }
}

class CustomerRepository extends BaseRepository<Customer> {
    @Override
    protected String getId(Customer item) { return item.id; }
}

class OrderRepository extends BaseRepository<Order> {
    @Override
    protected String getId(Order item) { return item.id; }
}

public class Main {
    public static void main(String[] args) {
        try {

            ProductRepository productRepo = new ProductRepository();
            CustomerRepository customerRepo = new CustomerRepository();
            OrderRepository orderRepo = new OrderRepository();

            Book b1 = new Book("B1", "Java Programming", 100, "James Gosling");
            Phone p1 = new Phone("P1", "iPhone 13", 2000, "Apple");
            Laptop l1 = new Laptop("L1", "Macbook Pro", 3000, "Apple");

            productRepo.add(b1);
            productRepo.add(p1);
            productRepo.add(l1);

            System.out.println("\n=== DANH SÁCH SẢN PHẨM ===");
            for (Product p : productRepo.findAll()) {
                System.out.println(p);
            }

            System.out.println("\n=== GIAO HÀNG ===");
            b1.deliver();
            p1.deliver();
            l1.deliver();

            System.out.println("\n=== HOÀN TIỀN ===");
            b1.refund();
            p1.refund();

            try {
                l1.refund();
            } catch (NonRefundableException e) {
                System.out.println("Lỗi hoàn tiền: " + e.getMessage());
            }

            Customer c1 = new Customer("C1", "Nguyễn Văn A");
            customerRepo.add(c1);

            Order order1 = new Order("O1", c1);
            order1.addProduct(b1);
            order1.addProduct(p1);

            orderRepo.add(order1);

            System.out.println("\n=== THANH TOÁN ===");
            Payment pay1 = new CreditCardPayment();
            pay1.pay(order1);

            Payment pay2 = new PaypalPayment();
            pay2.pay(order1);

            Payment pay3 = new MoMoPayment();
            pay3.pay(order1);

            System.out.println("\n=== TEST LỖI DuplicateIdException ===");
            try {
                productRepo.add(b1); // thêm lại B1 -> lỗi
            } catch (DuplicateIdException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("\n=== TEST LỖI InvalidPriceException ===");
            try {
                Book b2 = new Book("B2", "Sách lỗi", -10, "Tác giả ẩn danh");
            } catch (InvalidPriceException e) {
                System.out.println(e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Lỗi ngoài dự kiến: " + e.getMessage());
        }
    }
}
