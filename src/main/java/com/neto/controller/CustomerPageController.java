package com.neto.controller;

import com.neto.MainCustomer;
import com.neto.model.Customer;
import com.neto.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;


@Controller
public class CustomerPageController {

    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;


    @Autowired
    public CustomerPageController(RestTemplate restTemplate, CustomerRepository customerRepository) {

        this.restTemplate = restTemplate;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/listar")
    public String homePage(Model model) {
        ResponseEntity<List<Customer>> responseEntity =
                restTemplate.exchange("http://localhost:8080/api/v1/customers", HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Customer>>() {});

        List<Customer> customers = responseEntity.getBody();

        // Trate os objetos Customer como desejar
        // Exemplo: Filtre, ordene ou processe os dados

        model.addAttribute("customerList", customers);
        return "home"; // Retorna o nome da página a ser renderizada
    }

    @GetMapping("/adicionar")
    public String showAddCustomerForm(Model model
    ) {
        model.addAttribute("customer", new Customer());
        return "create";
    }
    @GetMapping("/atualizar/{customerId}")
    public String editCustomerForm(@PathVariable Integer customerId, Model model){
        try {
            Customer customer =  customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            model.addAttribute("customer", customer);
            return "update";
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Lança a exceção novamente para que o Spring possa tratá-la
        }
    }

    @PostMapping("/atualizar/{customerId}")
    public String updateCustomer(@PathVariable Integer customerId, @ModelAttribute Customer updatedCustomer) {
        // Busque o cliente existente pelo ID
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Atualize os dados do cliente existente com os dados do formulário
        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setAge(updatedCustomer.getAge());

        // Salve as atualizações no banco de dados
        customerRepository.save(existingCustomer);

        return "redirect:/listar"; // Redirecione para a página de listagem após a atualização
    }


    @GetMapping("/excluir/{customerId}")
    public String deleteCustomer(@PathVariable Integer customerId) {
        try {
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    "http://localhost:8080/api/v1/customers/" + customerId,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return "redirect:/listar"; // Redireciona para a página de listagem após a exclusão
            } else {
                // Trate o erro de exclusão de alguma forma, por exemplo, exibindo uma mensagem de erro
                return "error"; // Ou qualquer outra página de erro que você queira mostrar
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Trate a exceção de forma apropriada, você pode mostrar uma mensagem de erro ou fazer algo mais
            return "error"; // Ou qualquer outra página de erro que você queira mostrar
        }
    }







}