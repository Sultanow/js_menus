docker run -d --name payara -p 4848:4848 -p 8080:8080 -v $(pwd)/../../target/autodeploy:autodeploy payara/server-full
