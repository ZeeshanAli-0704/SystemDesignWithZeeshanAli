### What is Serverless?

Serverless computing is a cloud computing model where the cloud provider handles the provisioning, execution, and dynamic management of compute resources on behalf of the user. Despite its name, serverless computing does involve physical servers, but the key difference is that the complexity of server management is abstracted away from the user. 

In a serverless environment, users are billed only for the compute resources they consume when executing a service, unlike traditional cloud models where users need to maintain and pay for always-on virtual servers, even when they're idle. This model allows developers to focus solely on writing and deploying code without worrying about server maintenance.

For instance, traditionally, running applications in the cloud would require setting up multiple virtual servers to handle capacity, incurring costs even when the applications are not actively used. With serverless computing, the cloud provider ensures the right amount of compute resources are available to meet any demand without wastage.

Serverless computing also extends to other services like serverless databases, eliminating the need for provisioning or scaling database hardware. The cloud provider manages all the setup, maintenance, and scaling responsibilities, freeing up your staff to focus on core product development.

### Why Use Serverless Architectures?

Serverless architectures enable developers to concentrate on building and improving their products rather than managing servers or runtime environments, whether in the cloud or on-premises. This reduction in operational overhead allows developers to invest more time and energy into creating scalable and reliable products.

### How Serverless Architecture Works

Servers facilitate communication with applications and access to business logic, but managing servers requires significant effort and resources. This includes maintaining hardware, performing software and security updates, and creating backups. By adopting serverless architecture, developers offload these tasks to a third-party provider, allowing them to focus on writing and deploying application code.

A popular serverless architecture is Function as a Service (FaaS), where developers write application code as discrete functions, each performing a specific task triggered by events such as incoming emails or HTTP requests. After testing, these functions and their triggers are deployed to a cloud provider. When invoked, the cloud provider executes the function on a running server or spins up a new server if necessary, abstracting this process from developers.

### Fundamental Concepts in Serverless Architecture

- **Invocation:** A single function execution.
- **Duration:** The time taken for a serverless function to execute.
- **Cold Start:** Latency occurring when a function is triggered for the first time or after inactivity.
- **Concurrency Limit:** The maximum number of function instances that can run simultaneously in one region, as determined by the cloud provider.
- **Timeout:** The time allowed by the cloud provider for a function to run before termination. Providers set both default and maximum timeouts.

### Serverless Architecture vs. Container Architecture

Both serverless and container architectures abstract away the host environment but have distinct differences:

- **Server Maintenance:** In container architectures, developers maintain and update each container and its dependencies. In serverless architectures, this is handled by the cloud provider.
- **Scaling:** Serverless applications scale automatically, while container architectures require orchestration platforms like Kubernetes for scaling.
- **Control:** Containers provide control over the operating system and runtime environment, making them suitable for consistently high-traffic applications or as a first step in cloud migration. Serverless functions are ideal for event-driven tasks such as payment processing.

### Serverless Architecture Use Cases

Serverless architecture excels in managing short-lived tasks and workloads with infrequent or unpredictable traffic. Key use cases include:

- **Trigger-Based Tasks:** User activities that trigger events, such as a user signing up on a website triggering a database change and a welcome email.
- **Building RESTful APIs:** Leveraging services like Amazon API Gateway with serverless functions to create scalable RESTful APIs.
- **Asynchronous Processing:** Handling background tasks such as rendering product information or transcoding videos without affecting application flow.
- **Security Checks:** Using functions to scan new containers for vulnerabilities or manage SSH verification and two-factor authentication.
- **Continuous Integration (CI) and Continuous Delivery (CD):** Automating stages in CI/CD pipelines, such as creating builds from code commits or running automated tests on pull requests.

Developers typically migrate to serverless in stages, initially moving some application parts to serverless while keeping the rest on traditional servers. Serverless architectures are easily extensible, allowing more functions to be added as needed.



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli