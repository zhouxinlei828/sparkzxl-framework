package com.github.sparkzxl.gateway.plugin.jwt.handle;

import com.github.sparkzxl.gateway.plugin.rule.RuleHandle;

import java.util.List;

/**
 * description: jwt RuleHandle
 *
 * @author zhouxinlei
 * @date 2022-01-08 17:37:47
 */
public class JwtRuleHandle implements RuleHandle {

    /**
     * converter, Jwt body content is assigned to the header.
     */
    private List<Convert> converter;

    /**
     * get converter.
     *
     * @return List
     */
    public List<Convert> getConverter() {
        return converter;
    }

    /**
     * set converter.
     *
     * @param converter converter
     */
    public void setConverter(final List<Convert> converter) {
        this.converter = converter;
    }

    @Override
    public String toString() {
        return "JwtRuleHandle{"
                + "converter=" + converter.toString()
                + '}';
    }

    public static class Convert {

        /**
         * jwt of body name.
         */
        private String jwtVal;

        /**
         * header name.
         */
        private String headerVal;

        /**
         * get jwtVal.
         *
         * @return jwtVal
         */
        public String getJwtVal() {
            return jwtVal;
        }

        /**
         * set jwtVal.
         *
         * @param jwtVal jwtVal
         */
        public void setJwtVal(final String jwtVal) {
            this.jwtVal = jwtVal;
        }

        /**
         * get headerVal.
         *
         * @return headerVal
         */
        public String getHeaderVal() {
            return headerVal;
        }

        /**
         * set headerVal.
         *
         * @param headerVal headerVal
         */
        public void setHeaderVal(final String headerVal) {
            this.headerVal = headerVal;
        }

        @Override
        public String toString() {
            return "Convert{"
                    + "jwtVal='" + jwtVal + '\''
                    + ", headerVal='" + headerVal + '\''
                    + '}';
        }
    }

}
