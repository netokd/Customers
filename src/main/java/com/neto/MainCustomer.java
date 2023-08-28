package com.neto;


import com.neto.model.Customer;
import com.neto.repository.CustomerRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/customers")
public class MainCustomer {

    private final CustomerRepository customerRepository;

    public MainCustomer(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    public static void main(String[] args){
        SpringApplication.run(MainCustomer.class, args);
    }

    @GetMapping
    public List<Customer> getCustomers(){
        return customerRepository.findAll() ;
    }

    record NewCustomerRequest(
            String name,
            String email,
            Integer age
    ){}

    @PostMapping
    public void addCustomer(@RequestBody NewCustomerRequest request){
            Customer customer = new Customer();
            customer.setName(request.name());
            customer.setEmail(request.email());
            customer.setAge(request.age());
            customerRepository.save(customer);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer id){
        customerRepository.deleteById(id);

    }

    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer id){

    }

    @GetMapping("/view")
    public String viewCustomers(Model model){
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "home"; // Nome do arquivo HTML Thymeleaf
    }
}
