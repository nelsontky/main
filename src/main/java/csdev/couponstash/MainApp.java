package csdev.couponstash;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import csdev.couponstash.commons.core.Config;
import csdev.couponstash.commons.core.LogsCenter;
import csdev.couponstash.commons.core.Version;
import csdev.couponstash.commons.exceptions.DataConversionException;
import csdev.couponstash.commons.util.ConfigUtil;
import csdev.couponstash.commons.util.StringUtil;
import csdev.couponstash.logic.Logic;
import csdev.couponstash.logic.LogicManager;
import csdev.couponstash.model.CouponStash;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.ModelManager;
import csdev.couponstash.model.ReadOnlyCouponStash;
import csdev.couponstash.model.ReadOnlyUserPrefs;
import csdev.couponstash.model.UserPrefs;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.util.SampleDataUtil;
import csdev.couponstash.storage.CouponStashStorage;
import csdev.couponstash.storage.JsonCouponStashStorage;
import csdev.couponstash.storage.JsonUserPrefsStorage;
import csdev.couponstash.storage.Storage;
import csdev.couponstash.storage.StorageManager;
import csdev.couponstash.storage.UserPrefsStorage;
import csdev.couponstash.ui.Ui;
import csdev.couponstash.ui.UiManager;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 4, 0, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing CouponStash ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        CouponStashStorage couponStashStorage = new JsonCouponStashStorage(userPrefs.getCouponStashFilePath());
        storage = new StorageManager(couponStashStorage, userPrefsStorage);

        initLogging(config);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s CouponStash and {@code userPrefs}. <br>
     * The data from the sample CouponStash will be used instead if {@code storage}'s CouponStash is not found,
     * or an empty CouponStash will be used instead if errors occur when reading {@code storage}'s CouponStash.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        Optional<ReadOnlyCouponStash> couponStashOptional;
        ReadOnlyCouponStash initialData;
        try {
            couponStashOptional = storage.readCouponStash();
            if (couponStashOptional.isEmpty()) {
                logger.info("Data file not found. Will be starting with a sample CouponStash");
            }
            initialData = couponStashOptional.orElseGet(SampleDataUtil::getSampleCouponStash);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty CouponStash");
            initialData = new CouponStash();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty CouponStash");
            initialData = new CouponStash();
        }

        return new ModelManager(initialData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataConversionException e) {
            logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
                    + "Using default config properties");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using prefs file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataConversionException e) {
            logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
                    + "Using default user prefs");
            initializedPrefs = new UserPrefs();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty CouponStash");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting CouponStash " + MainApp.VERSION);
        List<Coupon> lastShownList = model.getFilteredCouponList();
        ui.start(primaryStage, lastShownList);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping CouponStash ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
