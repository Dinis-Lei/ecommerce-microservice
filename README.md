##  E-commerce website's Shopping Cart management

This project is a microservice for managing the Shopping Cart of an E-commerce website. The service provides a REST API to handle CRUD operations for cart items, including adding, updating, and removing items from the cart. It also includes features for clearing the cart, checking out, and obtaining analytics on shopping cart usage.

The microservice is built using Quarkus, ensuring high performance, minimal resource consumption, and readiness for Kubernetes deployment.

## Implemented features

### Backend API

- Cart and Item CRUD operations
- Cart checkout
- Cart clear items

### Analytics API

- Count number of carts, empty carts and carts with items
- Max, min and average items in all carts
- Top N items present inside carts 