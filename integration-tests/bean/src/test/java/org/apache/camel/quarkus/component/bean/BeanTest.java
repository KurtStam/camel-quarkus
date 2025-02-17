/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.bean;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.camel.quarkus.component.bean.model.Employee;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class BeanTest {
    @Test
    public void testRoutes() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("nuts@bolts")
                .post("/bean/route/process-order").then()
                .body(equalTo("{success=true, lines=[(id=1,item=nuts), (id=2,item=bolts)]}"));

        /* Ensure that the RoutesBuilder.configure() was not called multiple times on CamelRoute */
        RestAssured.when()
                .get("/bean/camel-configure-counter")
                .then()
                .statusCode(200)
                .body(equalTo("1"));
    }

    @Test
    public void named() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("Kermit")
                .post("/bean/route/named")
                .then()
                .body(equalTo("Hello Kermit from the NamedBean"));
    }

    @Test
    public void beanMethodInHeader() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("Kermit")
                .post("/bean/beanMethodInHeader")
                .then()
                .body(equalTo("Hi Kermit from the NamedBean"));
    }

    @Test
    public void method() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("Kermit")
                .post("/bean/route/method")
                .then()
                .body(equalTo("Hello Kermit from the MyBean"));
    }

    @Test
    public void handler() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("Kermit")
                .post("/bean/route/handler")
                .then()
                .body(equalTo("Hello Kermit from the WithHandlerBean"));
    }

    @Test
    public void handlerWithProxy() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("Kermit")
                .post("/bean/route/handlerOnProxy")
                .then()
                .body(equalTo("Hello Kermit from the WithHandlerBean"));
    }

    @Test
    public void inject() {

        /* Ensure that @Inject works */
        RestAssured.when().get("/bean/counter").then().body(equalTo("0"));
        RestAssured.when().get("/bean/route-builder-injected-count").then().body(equalTo("0"));
        RestAssured.when().get("/bean/route/increment").then().body(equalTo("1"));
        RestAssured.when().get("/bean/counter").then().body(equalTo("1"));
        RestAssured.when().get("/bean/route-builder-injected-count").then().body(equalTo("1"));
        RestAssured.when().get("/bean/route/increment").then().body(equalTo("2"));
        RestAssured.when().get("/bean/counter").then().body(equalTo("2"));
        RestAssured.when().get("/bean/route-builder-injected-count").then().body(equalTo("2"));

        /* Ensure that @ConfigProperty works */
        RestAssured.when()
                .get("/bean/route/config-property")
                .then()
                .statusCode(200)
                .body(equalTo("myFooValue = foo"));

        /* Ensure that the bean was not instantiated multiple times */
        RestAssured.when()
                .get("/bean/route-builder-instance-counter")
                .then()
                .statusCode(200)
                .body(equalTo("1"));

        /* Ensure that the RoutesBuilder.configure() was not called multiple times */
        RestAssured.when()
                .get("/bean/route-builder-configure-counter")
                .then()
                .statusCode(200)
                .body(equalTo("1"));
    }

    @Test
    public void lazy() {
        RestAssured.when().get("/bean/route/lazy").then().body(equalTo("lazy"));
    }

    @Test
    public void withProducer() {
        RestAssured.when().get("/bean/route/with-producer").then().body(equalTo("with-producer"));
    }

    @Test
    public void withLanguageParamBindings() {
        RestAssured.when().get("/bean/route/with-language-param-bindings").then()
                .body(equalTo("wlpb-hello(wlpb-route-31wp,cflap-bean-31wp)"));
    }

    @Disabled("https://github.com/apache/camel-quarkus/issues/2539")
    @Test
    public void consumeAnnotation() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("foo")
                .post("/bean/route/consumeAnnotation")
                .then()
                .body(equalTo("Consumed foo"));
    }

    @Disabled("https://github.com/apache/camel-quarkus/issues/2539")
    @Test
    public void endpointInject() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("bar")
                .post("/bean/endpointInject")
                .then()
                .body(equalTo("Sent to an @EndpointInject: bar"));
    }

    @Test
    public void methodWithExchangeArg() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new Employee("Joe", "Doe", "senior"))
                .post("/bean/employee/methodWithExchangeArg")
                .then()
                .body(equalTo("Hello Joe"));
    }

    @Test
    public void completionStageBean() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("Franz")
                .post("/bean/route/completionStageBean")
                .then()
                .body(equalTo("Hello Franz from CompletionStageBean"));
    }

    @Test
    public void multiArgMethod() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("Max")
                .post("/bean/route/multiArgMethod")
                .then()
                .body(equalTo("Hello Max from multiArgMethod: got exchange got registry"));
    }

    @Test
    public void parameterBindingAnnotations() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("Umberto")
                .post("/bean/parameterBindingAnnotations/Ciao")
                .then()
                .body(equalTo("Ciao Umberto from parameterBindingAnnotations"));
    }

    @Test
    public void parameterLiterals() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("Leon")
                .post("/bean/route/parameterLiterals")
                .then()
                .body(equalTo("Hello Leon from parameterLiterals(*, true)"));
    }

    @Test
    public void parameterTypes() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new Employee("Joe", "Doe", "senior"))
                .post("/bean/employee/parameterTypes")
                .then()
                .body(equalTo("employeeAsString: Employee [firstName=Joe, lastName=Doe, seniority=senior]"));
    }

}
