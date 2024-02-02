package tp6bis.helpers;

import com.github.javafaker.Faker;

import java.util.Locale;

public class CreateRandomData {
    private final Faker faker = new Faker(new Locale("en"));

    public String createRandomCompanyName() {
        return faker.company().name();
    }

    public double createRandomPoids(){
        return faker.random().nextDouble();
    }

}