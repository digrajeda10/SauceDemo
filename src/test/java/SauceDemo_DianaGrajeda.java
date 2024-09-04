import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SauceDemo_DianaGrajeda {

    private WebDriver driver;
    private WebDriverWait wait;

    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.saucedemo.com/v1/");
    }

    public void login() throws InterruptedException {
        WebElement userNameTextBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("user-name")));
        userNameTextBox.sendKeys("standard_user");

        WebElement passwordTextBox = driver.findElement(By.id("password"));
        passwordTextBox.sendKeys("secret_sauce");

        WebElement loginButton = driver.findElement(By.id("login-button"));
        loginButton.click();
        Thread.sleep(2000); //Pongo estos sleep para ver como se va corriendo la prueba
    }

    @Test
    public void agregarDosProductosYVerificar() throws InterruptedException {
        setup();
        login();

        // Agregar el primer producto "Sauce Labs Backpack" al carrito desde la página principal
        WebElement addToCartBackpack = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Sauce Labs Backpack']/following::button[contains(@class, 'btn_inventory')]")));
        addToCartBackpack.click();

        // Navegar a la página de detalles del producto "Sauce Labs Bike Light"
        WebElement bikeLightLink = driver.findElement(By.xpath("//div[text()='Sauce Labs Bike Light']/ancestor::a"));
        bikeLightLink.click();

        // Agregar el segundo producto "Sauce Labs Bike Light" al carrito desde la página de detalles
        WebElement addToCartBikeLight = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'btn_inventory')]")));
        addToCartBikeLight.click();

        // Navegar al carrito
        WebElement cartButton = driver.findElement(By.id("shopping_cart_container"));
        cartButton.click();

        // Verificar que ambos productos están en el carrito
        WebElement cartItem1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()='Sauce Labs Backpack']")));
        WebElement cartItem2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()='Sauce Labs Bike Light']")));
        Assertions.assertTrue(cartItem1.isDisplayed(), "El producto 'Sauce Labs Backpack' no se encontró en el carrito.");
        Assertions.assertTrue(cartItem2.isDisplayed(), "El producto 'Sauce Labs Bike Light' no se encontró en el carrito.");

        System.out.println("Test completado: Los productos 'Sauce Labs Backpack' y 'Sauce Labs Bike Light' se han agregado y verificado correctamente en el carrito.");
        driver.quit();
    }


    @Test
    public void eliminarArticulosDelCarritoYVerificar() throws InterruptedException {
        setup();
        login();

        // Agregar dos productos al carrito desde la página principal
        WebElement addToCartJacket = driver.findElement(By.xpath("//div[text()='Sauce Labs Fleece Jacket']/following::button[contains(@class, 'btn_inventory')]"));
        addToCartJacket.click();
        WebElement addToCartBikeLight = driver.findElement(By.xpath("//div[text()='Sauce Labs Bike Light']/following::button[contains(@class, 'btn_inventory')]"));
        addToCartBikeLight.click();

        // Navegar a la página de detalles del producto "Sauce Labs Fleece Jacket" y eliminar el producto
        WebElement jacketLink = driver.findElement(By.xpath("//div[text()='Sauce Labs Fleece Jacket']/ancestor::a"));
        jacketLink.click();
        WebElement removeButtonJacket = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'btn_secondary')]")));
        removeButtonJacket.click();

        // Volver a la página del carrito
        WebElement cartButton = driver.findElement(By.id("shopping_cart_container"));
        cartButton.click();

        // Eliminar el producto "Sauce Labs Bike Light" desde la página del carrito
        WebElement removeButtonBikeLight = driver.findElement(By.xpath("//div[text()='Sauce Labs Bike Light']/following::button[contains(@class, 'btn_secondary')]"));
        removeButtonBikeLight.click();

        // Verificar que el carrito esté vacío
        List<WebElement> cartItems = driver.findElements(By.className("cart_item"));
        if (cartItems.isEmpty()) {
            System.out.println("El carrito está vacío después de eliminar ambos productos.");
        } else {
            System.out.println("El carrito NO está vacío después de eliminar ambos productos.");
        }

        driver.quit();
    }


    @Test
    public void ordenarProductosPorPrecioYVerificar() throws InterruptedException {
        setup();
        login();

        // Encontrar el menú desplegable de ordenamiento y seleccionar "Price (low to high)"
        WebElement sortDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.className("product_sort_container")));
        sortDropdown.click();
        WebElement sortLowToHigh = driver.findElement(By.xpath("//option[@value='lohi']"));
        sortLowToHigh.click();

        // Obtener los precios de los productos mostrados
        List<WebElement> priceElements = driver.findElements(By.className("inventory_item_price"));
        List<Double> actualPrices = new ArrayList<>();
        for (WebElement priceElement : priceElements) {
            String priceText = priceElement.getText().replace("$", "");
            actualPrices.add(Double.parseDouble(priceText));
        }

        // Crear una copia de la lista de precios y ordenarla manualmente para comparar
        List<Double> sortedPrices = new ArrayList<>(actualPrices);
        Collections.sort(sortedPrices);

        // Verificar que los precios en la página estén ordenados de menor a mayor
        Assertions.assertEquals(sortedPrices, actualPrices, "Los productos no están ordenados correctamente por precio de menor a mayor.");
        System.out.println("Los productos están correctamente ordenados por precio de menor a mayor.");

        driver.quit();
    }


    @Test
    public void validarProcesoDePago() throws InterruptedException {
        setup();
        login();

        // Agregar dos productos al carrito
        WebElement addToCartBackpack = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Sauce Labs Backpack']/following::button[contains(@class, 'btn_inventory')]")));
        addToCartBackpack.click();
        WebElement addToCartOnesie = driver.findElement(By.xpath("//div[text()='Sauce Labs Onesie']/following::button[contains(@class, 'btn_inventory')]"));
        addToCartOnesie.click();

        // Navegar al carrito
        WebElement cartButton = driver.findElement(By.id("shopping_cart_container"));
        cartButton.click();

        // Proceder al pago usando el botón "Checkout"
        WebElement checkoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn_action.checkout_button")));
        checkoutButton.click();

        // Rellenar la información de pago
        WebElement firstNameField = driver.findElement(By.id("first-name"));
        firstNameField.sendKeys("Diana");
        WebElement lastNameField = driver.findElement(By.id("last-name"));
        lastNameField.sendKeys("Grajeda");
        WebElement postalCodeField = driver.findElement(By.id("postal-code"));
        postalCodeField.sendKeys("12345");

        // Continuar al siguiente paso usando el botón "Continue"
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.btn_primary.cart_button")));
        continueButton.click();

        // Verificar el subtotal y el total incluyendo impuestos
        WebElement itemTotalElement = driver.findElement(By.className("summary_subtotal_label"));
        WebElement taxElement = driver.findElement(By.className("summary_tax_label"));
        WebElement totalElement = driver.findElement(By.className("summary_total_label"));

        // Extraer los valores y calcular el total esperado
        String itemTotalText = itemTotalElement.getText().replace("Item total: $", "");
        String taxText = taxElement.getText().replace("Tax: $", "");
        String totalText = totalElement.getText().replace("Total: $", "");

        double itemTotal = Double.parseDouble(itemTotalText);
        double tax = Double.parseDouble(taxText);
        double expectedTotal = itemTotal + tax;
        double actualTotal = Double.parseDouble(totalText);

        // Verificar que el total calculado es correcto
        Assertions.assertEquals(expectedTotal, actualTotal, 0.01, "El monto total no está calculado correctamente.");
        System.out.println("El monto total está calculado correctamente, incluyendo impuestos.");

        // Pulsar el botón "Finish"
        WebElement finishButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn_action.cart_button")));
        finishButton.click();

        // Verificar que se muestra la página "THANK YOU FOR YOUR ORDER"
        WebElement thankYouMessage = driver.findElement(By.cssSelector("h2.complete-header"));
        Assertions.assertTrue(thankYouMessage.isDisplayed(), "El mensaje de 'THANK YOU FOR YOUR ORDER' no se mostró correctamente.");
        System.out.println("El mensaje de 'THANK YOU FOR YOUR ORDER' se mostró correctamente.");

        driver.quit();
    }


    @Test
    public void restablecerEstadoDeAplicacion() throws InterruptedException {
        setup();
        login();

        // Agregar un par de productos al carrito usando XPath
        WebElement addToCartBackpack = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Sauce Labs Bike Light']/following::button[contains(@class, 'btn_inventory')]")));
        addToCartBackpack.click();
        WebElement addToCartOnesie = driver.findElement(By.xpath("//div[text()='Sauce Labs Bolt T-Shirt']/following::button[contains(@class, 'btn_inventory')]"));
        addToCartOnesie.click();

        // Aplicar un filtro (ordenar por precio de mayor a menor)
        WebElement sortDropdown = driver.findElement(By.className("product_sort_container"));
        sortDropdown.click();
        WebElement sortHighToLow = driver.findElement(By.xpath("//option[@value='hilo']"));
        sortHighToLow.click();

        // Restablecer el estado de la aplicación
        WebElement menuButton = driver.findElement(By.xpath("//button[text()='Open Menu']"));
        menuButton.click();
        WebElement resetAppStateButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("reset_sidebar_link")));
        resetAppStateButton.click();

        // Cerrar el menú después del reset
        WebElement closeMenuButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@style='position: absolute; left: 0px; top: 0px; width: 100%; height: 100%; margin: 0px; padding: 0px; border: none; font-size: 0px; background: transparent; color: transparent; outline: none; cursor: pointer;']")));
        closeMenuButton.click();

        // Verificar que el filtro aplicado se haya restablecido (este es el comportamiento incorrecto esperado)
        WebElement sortDropdownAfterReset = driver.findElement(By.className("product_sort_container"));
        String selectedOption = sortDropdownAfterReset.getAttribute("value");

        if (selectedOption.equals("hilo")) {
            System.out.println("Error: El filtro de orden no se restableció después de 'Reset App State'.");
        } else {
            System.out.println("El filtro de orden se restableció correctamente después de 'Reset App State'.");
        }

        // Verificar que los botones "Remove" han sido restablecidos a "Add to Cart" (lo cual no sucede y es un error)
        WebElement addToCartButtonBikeLight = driver.findElement(By.xpath("//div[text()='Sauce Labs Bike Light']/following::button[contains(@class, 'btn_inventory')]"));
        String bikeLightButtonText = addToCartButtonBikeLight.getText();

        WebElement addToCartButtonOnesie = driver.findElement(By.xpath("//div[text()='Sauce Labs Bolt T-Shirt']/following::button[contains(@class, 'btn_inventory')]"));
        String onesieButtonText = addToCartButtonOnesie.getText();

        if (bikeLightButtonText.equals("ADD TO CART") && onesieButtonText.equals("ADD TO CART")) {
            System.out.println("Los botones se restablecieron correctamente a 'Add to Cart'.");
        } else {
            System.out.println("Error: Los botones 'Remove' no se restablecieron a 'Add to Cart' después de 'Reset App State'.");
        }

        // Verificar que el carrito esté vacío
        WebElement cartButton = driver.findElement(By.id("shopping_cart_container"));
        cartButton.click();
        boolean isCartEmpty = driver.findElements(By.className("cart_item")).isEmpty();

        if (isCartEmpty) {
            System.out.println("El carrito está vacío después de restablecer el estado de la aplicación.");
        } else {
            System.out.println("El carrito NO está vacío después de restablecer el estado de la aplicación.");
        }
        driver.quit();
    }


    @Test
    public void verificarProductosPersistenEnCarritoTrasLogout() throws InterruptedException {
        setup();
        login();

        // Agregar tres productos al carrito usando XPath
        WebElement addToCartBackpack = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Sauce Labs Backpack']/following::button[contains(@class, 'btn_inventory')]")));
        addToCartBackpack.click();
        WebElement addToCartOnesie = driver.findElement(By.xpath("//div[text()='Sauce Labs Onesie']/following::button[contains(@class, 'btn_inventory')]"));
        addToCartOnesie.click();
        WebElement addToCartBikeLight = driver.findElement(By.xpath("//div[text()='Sauce Labs Bike Light']/following::button[contains(@class, 'btn_inventory')]"));
        addToCartBikeLight.click();

        // Navegar al carrito y verificar que los productos están presentes
        WebElement cartButton = driver.findElement(By.id("shopping_cart_container"));
        cartButton.click();

        List<WebElement> cartItems = driver.findElements(By.className("inventory_item_name"));
        Assertions.assertTrue(cartItems.stream().anyMatch(item -> item.getText().equals("Sauce Labs Backpack")), "El producto 'Sauce Labs Backpack' no se encontró en el carrito.");
        Assertions.assertTrue(cartItems.stream().anyMatch(item -> item.getText().equals("Sauce Labs Onesie")), "El producto 'Sauce Labs Onesie' no se encontró en el carrito.");
        Assertions.assertTrue(cartItems.stream().anyMatch(item -> item.getText().equals("Sauce Labs Bike Light")), "El producto 'Sauce Labs Bike Light' no se encontró en el carrito.");
        System.out.println("Los tres productos están presentes en el carrito.");

        // Hacer logout
        WebElement menuButton = driver.findElement(By.xpath("//*[@id=\"menu_button_container\"]/div/div[3]/div/button"));
        menuButton.click();
        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link")));
        logoutButton.click();

        // Volver a iniciar sesión
        login();

        // Navegar al carrito y verificar que los productos aún están presentes
        cartButton = driver.findElement(By.id("shopping_cart_container"));
        cartButton.click();

        cartItems = driver.findElements(By.className("inventory_item_name"));
        Assertions.assertTrue(cartItems.stream().anyMatch(item -> item.getText().equals("Sauce Labs Backpack")), "El producto 'Sauce Labs Backpack' no se encontró en el carrito después de cerrar sesión.");
        Assertions.assertTrue(cartItems.stream().anyMatch(item -> item.getText().equals("Sauce Labs Onesie")), "El producto 'Sauce Labs Onesie' no se encontró en el carrito después de cerrar sesión.");
        Assertions.assertTrue(cartItems.stream().anyMatch(item -> item.getText().equals("Sauce Labs Bike Light")), "El producto 'Sauce Labs Bike Light' no se encontró en el carrito después de cerrar sesión.");
        System.out.println("Los productos siguen presentes en el carrito después de cerrar y volver a iniciar sesión.");
        driver.quit();
    }
}