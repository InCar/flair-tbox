package com.incarcloud.saic;

import com.incarcloud.saic.config.SAIC2017Config;
import com.incarcloud.saic.heliosphere.Parker;
import com.incarcloud.saic.meta.MetaVinMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App implements CommandLineRunner{
    private static final Logger s_logger = LoggerFactory.getLogger(App.class);
    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }

    @Autowired
    private ApplicationContext _ctx;

    @Override
    public void run(String... args)throws Exception{
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        s_logger.info("appver: {}", (new GitVer()).getVersion());
        s_logger.info("Active log4j config file: {}", config.getName());

        SAIC2017Config cfg = _ctx.getBean(SAIC2017Config.class);
        // 车辆静态配置
        MetaVinMode metaVinMode = new MetaVinMode();
        metaVinMode.load(cfg.getModes(), cfg.getVinMatch());

        // 主处理逻辑
        Parker parker = new Parker();
        parker.setJob(cfg.getBeginDate(), cfg.getEndDate(), metaVinMode);
        parker.onFinished((exitCode)->{
            s_logger.info("exit {}", exitCode);
            SpringApplication.exit(_ctx, ()->exitCode);
        });
        parker.setMaxPower(cfg.getMaxPower());
        parker.setDataSourceConfig(cfg.getMongo());
        parker.runAsync();
    }
}
