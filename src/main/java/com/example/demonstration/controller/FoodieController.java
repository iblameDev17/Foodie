package com.example.demonstration.controller;

import com.example.demonstration.model.User;
import com.example.demonstration.model.FoodItem;
import com.example.demonstration.repository.UserRepository;
import com.example.demonstration.repository.FoodRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FoodieController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodRepository foodRepository;

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
            // Initializing cart with Map structure
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

    // --- SHOPPING ROUTES (DATABASE DRIVEN) ---

    @GetMapping("/menu")
    public String menu(@RequestParam(required = false) Boolean vegOnly, 
                       @RequestParam(required = false) String search, 
                       HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        
        // --- UPDATED SEARCH & FILTER LOGIC ---
        List<FoodItem> foodItems;
        if (vegOnly != null) {
            foodItems = foodRepository.findByIsVeg(vegOnly);
        } else {
            foodItems = foodRepository.findAll();
        }

        if (search != null && !search.isEmpty()) {
            foodItems = foodItems.stream()
                .filter(i -> i.getName().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        model.addAttribute("user", user); // Pass full user object for the drawer
        model.addAttribute("userName", user.getName());
        model.addAttribute("foodItems", foodItems);
        model.addAttribute("cartSize", cart != null ? cart.size() : 0);
        return "menu";
    }

    @GetMapping("/item/{id}")
    public String viewItemDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        
        FoodItem item = foodRepository.findById(id).orElse(null);
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
        
        double subtotal = cart.stream()
            .mapToDouble(item -> ((Number) item.get("price")).doubleValue() * (int) item.get("quantity"))
            .sum();
            
        double deliveryCharge = (subtotal >= 500 || subtotal == 0) ? 0 : 50;
            
        model.addAttribute("userName", user.getName());
        model.addAttribute("cart", cart);
        model.addAttribute("subtotal", (int) subtotal);
        model.addAttribute("delivery", (int) deliveryCharge);
        model.addAttribute("total", (int) (subtotal + deliveryCharge));
        return "cart";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam Long itemId, @RequestParam String itemName, @RequestParam Double itemPrice, 
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

    @PostMapping("/increaseQuantity")
    public String increaseQuantity(@RequestParam Long itemId, HttpSession session) {
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart != null) {
            for (Map<String, Object> item : cart) {
                if (item.get("id").equals(itemId)) {
                    int currentQty = (int) item.get("quantity");
                    item.put("quantity", currentQty + 1);
                    break;
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/removeFromCart")
    public String removeFromCart(@RequestParam Long itemId, HttpSession session) {
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

    // --- CHECKOUT & ORDER ROUTES ---

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/menu";

        double subtotal = cart.stream()
            .mapToDouble(item -> ((Number) item.get("price")).doubleValue() * (int) item.get("quantity"))
            .sum();
        double delivery = (subtotal >= 500) ? 0 : 50;

        model.addAttribute("userName", user.getName());
        model.addAttribute("total", (int)(subtotal + delivery));
        // Pass address from DB to checkout if available
        model.addAttribute("userAddress", user.getAddress()); 
        return "checkout";
    }

    @PostMapping("/placeOrder")
    public String processOrder(@RequestParam String address, @RequestParam String paymentMethod, HttpSession session, RedirectAttributes ra) {
        session.setAttribute("cart", new ArrayList<Map<String, Object>>());
        ra.addFlashAttribute("address", address);
        return "redirect:/orderSuccess";
    }

    @GetMapping("/orderSuccess")
    public String orderSuccess(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        model.addAttribute("userName", user.getName());
        return "order-success";
    }

    // --- NEW PROFILE MANAGEMENT ROUTES ---

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam String address, @RequestParam String profilePic, HttpSession session, RedirectAttributes ra) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            currentUser.setAddress(address);
            currentUser.setProfilePic(profilePic);
            userRepository.save(currentUser);
            session.setAttribute("user", currentUser); // Update session with new data
            ra.addFlashAttribute("success", "Profile updated successfully!");
        }
        return "redirect:/menu";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpSession session, RedirectAttributes ra) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            if (currentUser.getPassword().equals(oldPassword)) {
                currentUser.setPassword(newPassword);
                userRepository.save(currentUser);
                session.setAttribute("user", currentUser);
                ra.addFlashAttribute("success", "Password changed successfully!");
            } else {
                ra.addFlashAttribute("error", "Incorrect old password!");
            }
        }
        return "redirect:/menu";
    }
}