/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.cucumber;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * @author Luce Sandfort and Wouter Timmermans
 */
@RunWith(Cucumber.class)
@CucumberOptions(monochrome = true, plugin = {"html:target/cucumber-html-report", "json:target/cucumber-json-report.json"})
public class FourCharmIT {


}
