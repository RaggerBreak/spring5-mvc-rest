package guru.springfamework.services;

import guru.springfamework.api.v1.mapper.CustomerMapper;
import guru.springfamework.api.v1.model.CustomerDTO;
import guru.springfamework.domain.Customer;
import guru.springfamework.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    public static final String BASE_URL = "/api/v1/customers";

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository
                .findAll()
                .stream()
                .map(customer -> {
                    CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);
                    customerDTO.setCustomerUrl(getCustomerUrl(customer.getId()));
                    return customerDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {

        return customerRepository
                .findById(id)
                .map(customer -> {
                    CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);
                    customerDTO.setCustomerUrl(getCustomerUrl(customer.getId()));
                    return customerDTO;
                })
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public CustomerDTO createNewCustomer(CustomerDTO customerDTO) {

        return  saveAndReturnDTO(customerMapper.customerDtoToCustomer(customerDTO));
    }


    @Override
    public CustomerDTO saveCustomerByDTO(Long id, CustomerDTO customerDTO) {

        Customer customer = customerMapper.customerDtoToCustomer(customerDTO);
        customer.setId(id);

        return saveAndReturnDTO(customer);
    }

    private CustomerDTO saveAndReturnDTO(Customer customer){

        Customer savedCustomer = customerRepository.save(customer);

        CustomerDTO returnDto = customerMapper.customerToCustomerDTO(savedCustomer);

        returnDto.setCustomerUrl(getCustomerUrl(savedCustomer.getId()));

        return returnDto;
    }

    @Override
    public CustomerDTO patchCustomer(Long id, CustomerDTO customerDTO) {

        return customerRepository.findById(id).map(customer -> {
            if (customerDTO.getFirstname() != null){
                customer.setFirstname(customerDTO.getFirstname());
            }

            if (customerDTO.getLastname() != null){
                customer.setLastname(customerDTO.getLastname());
            }

            CustomerDTO returnDto = customerMapper.customerToCustomerDTO(customerRepository.save(customer));
            returnDto.setCustomerUrl(getCustomerUrl(id));

            return returnDto;

        }).orElseThrow(RuntimeException::new);
    }

    private String getCustomerUrl(Long id){
        return BASE_URL + "/" + id;
    }

    @Override
    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }


}
