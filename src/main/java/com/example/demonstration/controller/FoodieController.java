package com.example.demonstration.controller;

import com.example.demonstration.model.User;
import com.example.demonstration.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class FoodieController {

    @Autowired
    private UserRepository userRepository;

    // Helper method to simulate a product database
    private List<Map<String, Object>> getFoodItems() {
        List<Map<String, Object>> items = new ArrayList<>();
        items.add(createFoodItem(1, "Classic Cheeseburger", 249, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500", "Juicy patty with melted cheddar and fresh veggies."));
        items.add(createFoodItem(2, "Margherita Pizza", 399, "https://images.unsplash.com/photo-1604068549290-dea0e4a305ca?w=500", "Fresh mozzarella, basil, and tomato sauce."));
        items.add(createFoodItem(3, "Spicy Chicken Wings", 299, "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=500", "Crispy wings with our secret spice blend."));
        items.add(createFoodItem(4, "Caesar Salad", 199, "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500", "Crisp romaine with parmesan and croutons."));
        items.add(createFoodItem(5, "Grilled Fish Tacos", 349, "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=500", "Seasoned grilled fish with cabbage slaw."));
        items.add(createFoodItem(6, "Butter Chicken", 349, "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=500", "Creamy tomato sauce with tender chicken pieces."));
        items.add(createFoodItem(7, "Chocolate Brownie", 149, "https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=500", "Rich chocolate fudge brownie with ice cream."));
        items.add(createFoodItem(8, "Veggie Biryani", 279, "https://images.unsplash.com/photo-1645112411341-6c4ee32510d8?w=500", "Fragrant basmati rice with mixed vegetables."));
        return items;
    }

    private Map<String, Object> createFoodItem(int id, String name, int price, String image, String description) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", id);
        item.put("name", name);
        item.put("price", price);
        item.put("image", image);
        item.put("description", description);
        return item;
    }

    // --- AUTHENTICATION ROUTES ---

    @GetMapping("/")
    public String index(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, RedirectAttributes ra) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            session.setAttribute("cart", new ArrayList<Map<String, Object>>());
            return "redirect:/menu";
        }
        ra.addFlashAttribute("loginError", "Invalid Username or Password");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "register";
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }
        userRepository.save(user);
        ra.addFlashAttribute("regSuccess", "Account created! Please login.");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- SHOPPING ROUTES ---

    @GetMapping("/menu")
    public String menu(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        model.addAttribute("userName", user.getName());
        model.addAttribute("foodItems", getFoodItems());
        model.addAttribute("cartSize", cart != null ? cart.size() : 0);
        return "menu";
    }

    @GetMapping("/item/{id}")
    public String viewItemDetail(@PathVariable int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        
        Map<String, Object> item = getFoodItems().stream()
            .filter(i -> (int) i.get("id") == id)
            .findFirst()
            .orElse(null);
        
        if (item == null) return "redirect:/menu";
        
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        model.addAttribute("userName", user.getName());
        model.addAttribute("item", item);
        model.addAttribute("cartSize", cart != null ? cart.size() : 0);
        return "item-detail";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();
        
        int subtotal = cart.stream()
            .mapToInt(item -> (int) item.get("price") * (int) item.get("quantity"))
            .sum();
            
        model.addAttribute("userName", user.getName());
        model.addAttribute("cart", cart);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("delivery", 50);
        model.addAttribute("total", subtotal + 50);
        return "cart";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam int itemId, @RequestParam String itemName, @RequestParam int itemPrice, 
                           @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        
        boolean found = false;
        for (Map<String, Object> item : cart) {
            if (item.get("id").equals(itemId)) {
                int currentQty = (int) item.get("quantity");
                item.put("quantity", currentQty + quantity);
                found = true;
                break;
            }
        }
        
        if (!found) {
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("id", itemId);
            newItem.put("name", itemName);
            newItem.put("price", itemPrice);
            newItem.put("quantity", quantity);
            cart.add(newItem);
        }
        return "redirect:/menu";
    }

    @PostMapping("/removeFromCart")
    public String removeFromCart(@RequestParam int itemId, HttpSession session) {
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.get("id").equals(itemId));
        }
        return "redirect:/cart";
    }

    @PostMapping("/clearCart")
    public String clearCart(HttpSession session) {
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart != null) {
            cart.clear();
        }
        return "redirect:/menu";
    }
}