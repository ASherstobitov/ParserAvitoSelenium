import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    static class CostComparator implements Comparator<Car> {

        @Override
        public int compare(Car o1, Car o2) {
            if (o1.getCost() > o2.getCost()) {
                return 1;
            } else if (o1.getCost() < o2.getCost()) {
                return -1;
            }
            return 0;
        }
    }

    public static void safeInFile(ArrayList<Car> cars){
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("Cars.bin"))) {
            outputStream.writeObject(cars);
        } catch (IOException e) {e.printStackTrace();}
    }
    public static ArrayList<Car> loadFromFile() {
        ArrayList<Car> cars = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Cars.bin"))) {
            cars =(ArrayList<Car>) ois.readObject();
        }catch (IOException e) {e.printStackTrace();} catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cars;
    }

    public static ArrayList<Car> readCar() {
        ArrayList<Car> cars = new ArrayList<Car>();

            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");
            ChromeDriver driver = new ChromeDriver();
        for (int i = 0; i < 2 ; i++) {

            driver.get("https://www.avito.ru/ekaterinburg/avtomobili?radius=200&p=" + (i + 1));
            WebElement element = driver.findElementByClassName("js-catalog_serp");
            List<WebElement> findElements = element.findElements(By.className("item_table"));
            for (WebElement item : findElements) {
                String title = item.findElement(By.className("snippet-title")).getText();
                String[] itemsTitle = title.split(",");
                String model = itemsTitle[0];
                int year = Integer.parseInt(itemsTitle[1].trim());
                String info = item.findElement(By.className("specific-params_block")).getText();
                String sKM = repairString(info.substring(0, info.indexOf("км")));

                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(sKM);
                matcher.find();
                sKM = sKM.substring(matcher.start(), matcher.end());

                int km = Integer.parseInt(sKM);
                String price = item.findElement(By.className("snippet-price-row")).getText();
                price = repairString(price.substring(0, price.length() - 2));
                int cost = Integer.parseInt(price);
                String city = item.findElement(By.className("item-address-georeferences-item__content")).getText();
                Car car = new Car(model, year, cost, km, city);
                cars.add(car);
            }
        }
            driver.close();

            return cars;

    }

    public static String repairString(String beg) {
        String s = beg.replaceAll("\\s", "").trim();
        return s;
    }



    public static void export(ArrayList<Car> cars) throws IOException {
        // Создание книги Excel
        XSSFWorkbook book = new XSSFWorkbook();
        FileOutputStream fileOut = new FileOutputStream("workbook.xlsx");

        XSSFSheet sheet = book.createSheet("Sheet 1");
        XSSFRow titles = sheet.createRow(0);
        titles.createCell(0).setCellValue("Model");
        titles.createCell(1).setCellValue("Year");
        titles.createCell(2).setCellValue("Cost");
        titles.createCell(3).setCellValue("Km");
        titles.createCell(4).setCellValue("City");
        CellStyle style = book.createCellStyle();
        XSSFFont font = book.createFont();
        font.setBold(true);
        style.setFont(font);
        for (int i = 0; i < 5 ; i++) {
            titles.getCell(i).setCellStyle(style);
        }



        for (int i = 0; i < cars.size(); i++) {
            XSSFRow row = sheet.createRow((short)i + 1);
            XSSFCell cellModel = row.createCell(0);
            cellModel.setCellType(CellType.STRING);
            cellModel.setCellValue(cars.get(i).getModel());
            XSSFCell cellYear = row.createCell(1);
            cellYear.setCellType(CellType.NUMERIC);
            cellYear.setCellValue(cars.get(i).getYear());
            XSSFCell cellCost = row.createCell(2);
            cellCost.setCellType(CellType.NUMERIC);
            cellCost.setCellValue(cars.get(i).getCost());
            XSSFCell cellKm = row.createCell(3);
            cellKm.setCellType(CellType.NUMERIC);
            cellKm.setCellValue(cars.get(i).getKm());
            XSSFCell cellCity = row.createCell(4);
            cellCity.setCellType(CellType.STRING);
            cellCity.setCellValue(cars.get(i).getCity());
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }










// создания страниц
// создание строк
// создание и форматирование ячеек
// запись информации в ячейки

// Закрытие
        book.write(fileOut);
        fileOut.close();
    }

    public static void showCars(ArrayList<Car> cars) {
        System.out.printf("%22s%6s%10s%10s%25s\n", "Model", "Year", "Cost", "Km", "City");
        for (Car car : cars) {
            System.out.printf("%22s%6d%10d%10d%25s\n", car.getModel(), car.getYear(), car.getCost(), car.getKm(), car.getCity());
        }
    }

    public static void main(String[] args) throws IOException {

//        ArrayList<Car> cars = readCar();
//        safeInFile(cars);
        ArrayList<Car> cars = loadFromFile();
        showCars(cars);
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("1. search the car by the cost");
            System.out.println("2. sort");
            System.out.println("3. The most expensive car");
            System.out.println("4. Show top five to cheap cars");
            System.out.println("5. Show top five to expensive cars");
            System.out.println("6. Show all cities");
            System.out.println("7. sum of all costs");



            int option = scanner.nextInt();
            if (option == 1) {
                System.out.println("Enter the start cost, and the end cost");
                final int start = scanner.nextInt();
                final int end = scanner.nextInt();
                ArrayList<Car> filterCars = cars.stream()
                        .filter(t-> t.getCost() > start && t.getCost() < end)
                        .collect(Collectors.toCollection(ArrayList::new));
                showCars(filterCars);
            } else if (option == 2) {
              ArrayList<Car> sortedCars = cars.stream()
                      .sorted(new CostComparator()).collect(Collectors.toCollection(ArrayList::new));
              showCars(sortedCars);
            } else if (option == 3) {
                Optional<Car> maxCar = cars.stream().max(new CostComparator());
                if (maxCar.isPresent()) {
                    Car car = maxCar.get();
                    ArrayList<Car> cars1 = new ArrayList<>();
                    cars1.add(car);
                    showCars(cars1);
                }
            } else if (option == 4) {
               ArrayList<Car> firstFiveCars = cars.stream()
                        .sorted(new CostComparator()).limit(5).collect(Collectors.toCollection(ArrayList::new));
               showCars(firstFiveCars);
            } else if (option == 5) {
                ArrayList<Car> firstFiveCars = cars.stream()
                        .sorted(new CostComparator()).skip(cars.size() - 5).collect(Collectors.toCollection(ArrayList::new));
                showCars(firstFiveCars);
            } else if (option == 6) {
               ArrayList<String> cities = cars.stream().map(t-> t.getCity()).distinct().collect(Collectors.toCollection(ArrayList::new));
               cities.stream().forEach(System.out::println);
            } else if (option == 7) {
               int sum = cars.stream().reduce(0, (cost, car)-> {return cost  + car.getCost();},(car1, car2)-> car1 + car2);
                System.out.println(sum);
            }

        } while (true);




    }
}
