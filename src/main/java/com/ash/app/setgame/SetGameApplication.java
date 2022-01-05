package com.ash.app.setgame;

import com.ash.app.setgame.service.GameLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SetGameApplication {

    @Autowired
    GameLogic gameLogic;

    public static void main(String[] args) {
        SpringApplication.run(SetGameApplication.class, args);
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            long start = System.nanoTime();

            // reading the file placed in the resources folder via the provided name in Program Args
            Scanner scn = new Scanner(ResourceUtils.getFile("classpath:"+args[0]));
            List<String> input = new ArrayList();
            //constructing list of strings using the file contents
            while(scn.hasNextLine()) {
                input.add(scn.nextLine());
            }

            gameLogic.convertInputToCardList(input);

            gameLogic.findValidCardSets();

            gameLogic.findLargestDisjointCollectionOfSets();

            long stop = System.nanoTime();
            //System.out.println(stop-start);
        };
    }

}
