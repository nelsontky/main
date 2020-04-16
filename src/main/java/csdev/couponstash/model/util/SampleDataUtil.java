package csdev.couponstash.model.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import csdev.couponstash.model.CouponStash;
import csdev.couponstash.model.ReadOnlyCouponStash;
import csdev.couponstash.model.coupon.Archived;
import csdev.couponstash.model.coupon.Condition;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.coupon.ExpiryDate;
import csdev.couponstash.model.coupon.Limit;
import csdev.couponstash.model.coupon.Name;
import csdev.couponstash.model.coupon.PromoCode;
import csdev.couponstash.model.coupon.RemindDate;
import csdev.couponstash.model.coupon.StartDate;
import csdev.couponstash.model.coupon.Usage;
import csdev.couponstash.model.coupon.savings.DateSavingsSumMap;
import csdev.couponstash.model.coupon.savings.MonetaryAmount;
import csdev.couponstash.model.coupon.savings.PercentageAmount;
import csdev.couponstash.model.coupon.savings.PureMonetarySavings;
import csdev.couponstash.model.coupon.savings.Saveable;
import csdev.couponstash.model.coupon.savings.Savings;
import csdev.couponstash.model.tag.Tag;

/**
 * Contains utility methods for populating {@code CouponStash} with sample data.
 */
public class SampleDataUtil {
    public static Coupon[] getSampleCoupons() {

        // Coupon names

        Name adidas = new Name("Adidas");
        Name gongCha = new Name("Gong Cha");
        Name shopee = new Name("Shopee");
        Name lazada = new Name("Lazada");
        Name liHo = new Name("LiHO");
        Name grabFood = new Name("GrabFood");

        // Coupon Promo Codes

        PromoCode adidasCode = new PromoCode("30ADIDAS");
        PromoCode gongChaCode = new PromoCode("ILOVEGONGCHA");
        PromoCode shopeeCode = new PromoCode("NEWUSER");
        PromoCode lazadaCode = new PromoCode("DBSFLASH");
        PromoCode liHoCode = new PromoCode("STUDENT");
        PromoCode grabFoodCode = new PromoCode("40OFF");

        // Coupon Savings

        ArrayList<Saveable> adidasSaveables = new ArrayList<Saveable>();
        adidasSaveables.add(new Saveable("Adidas Cap"));
        DateSavingsSumMap adidasSavingsSum = new DateSavingsSumMap();
        Savings adidasSavings = new Savings(new PercentageAmount(30d), adidasSaveables);

        DateSavingsSumMap gongChaSavingsSum = new DateSavingsSumMap();
        gongChaSavingsSum.add(LocalDate.of(2020, 3, 11),
                new PureMonetarySavings(new MonetaryAmount(7, 30)));
        gongChaSavingsSum.add(LocalDate.of(2020, 3, 18),
                new PureMonetarySavings(new MonetaryAmount(17, 0)));
        Savings gongChaSavings = new Savings(new PercentageAmount(10d));

        ArrayList<Saveable> shopeeSaveables = new ArrayList<Saveable>();
        shopeeSaveables.add(new Saveable("USB-C cable"));
        shopeeSaveables.add(new Saveable("iPhone case"));
        DateSavingsSumMap shopeeSavingsSum = new DateSavingsSumMap();
        shopeeSavingsSum.add(LocalDate.of(2020, 4, 1),
                new PureMonetarySavings(new MonetaryAmount(25, 20), shopeeSaveables));
        Savings shopeeSavings = new Savings(new MonetaryAmount(7, 0), shopeeSaveables);

        Savings lazadaSavings = new Savings(new PercentageAmount(20d));
        DateSavingsSumMap lazadaSavingsSum = new DateSavingsSumMap();
        lazadaSavingsSum.add(LocalDate.of(2020, 3, 25),
                new PureMonetarySavings(new MonetaryAmount(10, 50)));

        Savings liHoSavings = new Savings(new MonetaryAmount(1, 0));
        DateSavingsSumMap liHoSavingsSum = new DateSavingsSumMap();

        Savings grabFoodSavings = new Savings(new PercentageAmount(40d));
        DateSavingsSumMap grabFoodSavingsSum = new DateSavingsSumMap();

        // Coupon Expiry Date

        ExpiryDate adidasExpiry = new ExpiryDate("31-12-2020");
        ExpiryDate gongChaExpiry = new ExpiryDate("30-06-2020");
        ExpiryDate shopeeExpiry = new ExpiryDate("31-10-2020");
        ExpiryDate lazadaExpiry = new ExpiryDate("30-09-2020");
        ExpiryDate liHoExpiry = new ExpiryDate("31-05-2020");
        ExpiryDate grabFoodExpiry = new ExpiryDate("30-04-2020");

        // Coupon Start Date

        StartDate adidasStart = new StartDate("01-01-2020");
        StartDate gongChaStart = new StartDate("01-04-2020");
        StartDate shopeeStart = new StartDate("01-01-2020");
        StartDate lazadaStart = new StartDate("01-01-2020");
        StartDate liHoStart = new StartDate("01-04-2020");
        StartDate grabFoodStart = new StartDate("01-04-2020");

        // Coupon Usage

        Usage adidasUsage = new Usage(0);
        Usage gongChaUsage = new Usage(2);
        Usage shopeeUsage = new Usage(1);
        Usage lazadaUsage = new Usage(1);
        Usage liHoUsage = new Usage(0);
        Usage grabFoodUsage = new Usage(0);

        // Coupon Limit

        Limit adidasLimit = new Limit(1);
        Limit gongChaLimit = new Limit(3);
        Limit shopeeLimit = new Limit(2);
        Limit lazadaLimit = new Limit(5);
        Limit liHoLimit = new Limit(0);
        Limit grabFoodLimit = new Limit(3);

        // Coupon Remind Date

        RemindDate adidasRemind = new RemindDate("28-12-2020");
        RemindDate gongChaRemind = new RemindDate("27-06-2020");
        RemindDate shopeeRemind = new RemindDate("28-10-2020");
        RemindDate lazadaRemind = new RemindDate("27-09-2020");
        RemindDate liHoRemind = new RemindDate("28-05-2020");
        RemindDate grabFoodRemind = new RemindDate("27-04-2020");

        // Coupon Tags

        Set<Tag> adidasTags = getTagSet("sports");
        Set<Tag> gongChaTags = getTagSet("bubbletea");
        Set<Tag> shopeeTags = getTagSet("newuser");
        Set<Tag> lazadaTags = getTagSet("ecommerce");
        Set<Tag> liHoTags = getTagSet("bubbletea");
        Set<Tag> grabFoodTags = getTagSet("delivery", "grab", "food");

        // Coupon Condition

        Condition adidasCondition = new Condition("No condition stated.");
        Condition gongChaCondition = new Condition("While stocks lasts");
        Condition shopeeCondition = new Condition("New users only");
        Condition lazadaCondition = new Condition("While stocks lasts");
        Condition liHoCondition = new Condition("Show student card");
        Condition grabFoodCondition = new Condition("Min. order $20");

        // Coupon Archive

        Archived adidasArchive = new Archived(false);
        Archived gongChaArchive = new Archived(false);
        Archived shopeeArchive = new Archived(false);
        Archived lazadaArchive = new Archived(false);
        Archived liHoArchive = new Archived(false);
        Archived grabFoodArchive = new Archived(false);

        // Coupon

        Coupon adidasCoupon = new Coupon(adidas, adidasCode, adidasSavings, adidasExpiry, adidasStart, adidasUsage,
                adidasLimit, adidasTags, adidasSavingsSum, adidasRemind, adidasCondition, adidasArchive);
        Coupon gongChaCoupon = new Coupon(gongCha, gongChaCode, gongChaSavings, gongChaExpiry, gongChaStart,
                gongChaUsage, gongChaLimit, gongChaTags, gongChaSavingsSum, gongChaRemind,
                gongChaCondition, gongChaArchive);
        Coupon shopeeCoupon = new Coupon(shopee, shopeeCode, shopeeSavings, shopeeExpiry, shopeeStart, shopeeUsage,
                shopeeLimit, shopeeTags, shopeeSavingsSum, shopeeRemind, shopeeCondition, shopeeArchive);
        Coupon lazadaCoupon = new Coupon(lazada, lazadaCode, lazadaSavings, lazadaExpiry, lazadaStart, lazadaUsage,
                lazadaLimit, lazadaTags, lazadaSavingsSum, lazadaRemind, lazadaCondition, lazadaArchive);
        Coupon liHoCoupon = new Coupon(liHo, liHoCode, liHoSavings, liHoExpiry, liHoStart, liHoUsage,
                liHoLimit, liHoTags, liHoSavingsSum, liHoRemind, liHoCondition, liHoArchive);
        Coupon grabFoodCoupon = new Coupon(grabFood, grabFoodCode, grabFoodSavings, grabFoodExpiry, grabFoodStart,
                grabFoodUsage, grabFoodLimit, grabFoodTags, grabFoodSavingsSum, grabFoodRemind,
                grabFoodCondition, grabFoodArchive);

        // Coupons

        Coupon[] coupons = new Coupon[] {adidasCoupon, gongChaCoupon, shopeeCoupon, lazadaCoupon,
            liHoCoupon, grabFoodCoupon};

        return coupons;
    }

    public static ReadOnlyCouponStash getSampleCouponStash() {
        CouponStash sampleAb = new CouponStash();
        for (Coupon sampleCoupon : getSampleCoupons()) {
            sampleAb.addCoupon(sampleCoupon);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
            .map(Tag::new).collect(Collectors.toSet());
    }

}
