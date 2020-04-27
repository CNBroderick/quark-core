/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-04-15 15:15:31
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.util.mysql.RuleAndReplacement
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.mysql;

class RuleAndReplacement {
    private String rule;
    private String replacement;

    public RuleAndReplacement(String rule, String replacement) {
        this.rule = rule;
        this.replacement = replacement;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
