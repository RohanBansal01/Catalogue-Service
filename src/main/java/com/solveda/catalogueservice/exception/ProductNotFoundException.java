package com.solveda.catalogueservice.exception;

/**
 * Thrown when a {@link com.solveda.catalogueservice.model.Product}
 * cannot be found in the system.
 * <p>
 * Typically raised during retrieval, update, or delete operations
 * when a product with the specified identifier does not exist.
 * </p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * Product product = productRepository.findById(id)
 *     .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
 * }</pre>
 *
 * @see com.solveda.catalogueservice.repository.ProductRepository
 *
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Creates a new {@code ProductNotFoundException} with the specified detail message.
     *
     * @param message description of the product not found error
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}
