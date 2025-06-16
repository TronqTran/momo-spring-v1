# momo-spring-v1 🚀

A Spring Boot application integrating with the MoMo payment gateway. 💳

## Features ✨

- Create MoMo payment orders 📝
- Query transaction status 🔍
- Process refunds and query refund status 💸
- RESTful API endpoints 🌐

## Requirements 🛠️

- Java 17+ ☕
- Maven 3.6+ 🧰
- MoMo partner credentials 🔑

## Getting Started 🏁

1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-username/momo-spring-v1.git
   cd momo-spring-v1
   ```
2. **Configure application properties:**
    - Edit `src/main/resources/application.yml` or `application.properties` with your MoMo credentials:
      ```
      momo.config.partnerCode=YOUR_PARTNER_CODE
      momo.config.accessKey=YOUR_ACCESS_KEY
      momo.config.secretKey=YOUR_SECRET_KEY
      momo.config.requestType=YOUR_REQUEST_TYPE
      momo.config.createEndpoint=CREATE_ENDPOINT_URL
      momo.config.queryEndpoint=QUERY_ENDPOINT_URL
      momo.config.refundEndpoint=REFUND_ENDPOINT_URL
      momo.config.queryRefundEndpoint=QUERY_REFUND_ENDPOINT_URL
      momo.config.redirectUrl=YOUR_REDIRECT_URL
      momo.config.callBack=YOUR_CALLBACK_URL
      ```

3. **Build and run the application:**
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## API Endpoints 📡

- `POST /api/v1/momo/createOrder` 🆕  
  Create a payment order  
  Params: `amount`, `orderInfo`

- `GET /api/v1/momo/queryOrder` ❓  
  Query transaction status  
  Params: `orderId`

- `POST /api/v1/momo/refundOrder` 🔄  
  Refund a transaction  
  Params: `transactionId`, `amount`, `reason`

- `GET /api/v1/momo/queryRefundStatus` 🔁  
  Query refund status  
  Params: `orderId`

## Project Structure 🗂️

- `src/main/java/com/vn/momo/` - Main application 🏠
- `src/main/java/com/vn/momo/controller/` - REST controllers 🕹️
- `src/main/java/com/vn/momo/service/` - Business logic ⚙️
- `src/main/java/com/vn/momo/config/` - Configuration classes 🛡️
- `src/main/java/com/vn/momo/util/` - Utility classes 🧩

## Contributing 🤝

Pull requests are welcome. For major changes, please open an issue first. 🙏

## License 📄

This project is licensed under the MIT License. 📝
```