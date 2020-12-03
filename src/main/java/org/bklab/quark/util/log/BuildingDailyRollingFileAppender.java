/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-03-27 13:40:59
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.util.BuildingDailyRollingFileAppender
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.log;

/**
 * log4j appender扩展<br>
 * （1）按天并且只保留最近n天的 <br>
 * （2）如果一天的文件过大，可以按配置的大小将一天的文件进行切分
 *
 * @author Broderick
 * @since 2019-12-12
 */
@SuppressWarnings("WeakerAccess")
public class BuildingDailyRollingFileAppender extends DailyRollingFileAppender {
}

