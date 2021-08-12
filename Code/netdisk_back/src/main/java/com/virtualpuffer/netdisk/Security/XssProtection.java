package com.virtualpuffer.netdisk.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 调用上级方法获取值后处理再返回
* */
public class XssProtection extends HttpServletRequestWrapper {
        HttpServletRequest orgRequest = null;

        public XssProtection(HttpServletRequest request) {
            super(request);
            orgRequest = request;
        }
        /**
         * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
         * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
         * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
         */
        @Override
        public String getParameter(String name) {
            String value = super.getParameter(xssEncode(name));
            if (value != null) {
                value = xssEncode(value);
            }
            return value;
        }
        /**
         * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
         * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
         * getHeaderNames 也可能需要覆盖
         */
        @Override
        public String getHeader(String name) {
            String value = super.getHeader(xssEncode(name));
            if (value != null) {
                value = xssEncode(value);
            }
            return value;
        }
        /**
         * 将容易引起xss漏洞的半角字符直接替换成全角字符
         *
         * @param s
         * @return
         */
        private static String xssEncode(String s) {
            if (s == null || s.isEmpty()) {
                return s;
            }
            StringBuilder sb = new StringBuilder(s.length() + 16);
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '>':
                        sb.append('＞');// 全角大于号
                        break;
                    case '<':
                        sb.append('＜');// 全角小于号
                        break;
                    case '\'':
                        sb.append('‘');// 全角单引号
                        break;
                    case '\"':
                        sb.append('“');// 全角双引号
                        break;
                    case '&':
                        sb.append('＆');// 全角
                        break;
                    case '\\':
                        sb.append('＼');// 全角斜线
                        break;
                    case '#':
                        sb.append('＃');// 全角井号
                        break;
                    case '%':    // < 字符的 URL 编码形式表示的 ASCII 字符（十六进制格式） 是: %3c
                        processUrlEncoder(sb, s, i);
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
            return sb.toString();
        }
        public static void processUrlEncoder(StringBuilder sb, String s, int index){
            if(s.length() >= index + 2){
                if(s.charAt(index+1) == '3' && (s.charAt(index+2) == 'c' || s.charAt(index+2) == 'C')){    // %3c, %3C
                    sb.append('＜');
                    return;
                }
                if(s.charAt(index+1) == '6' && s.charAt(index+2) == '0'){    // %3c (0x3c=60)
                    sb.append('＜');
                    return;
                }
                if(s.charAt(index+1) == '3' && (s.charAt(index+2) == 'e' || s.charAt(index+2) == 'E')){    // %3e, %3E
                    sb.append('＞');
                    return;
                }
                if(s.charAt(index+1) == '6' && s.charAt(index+2) == '2'){    // %3e (0x3e=62)
                    sb.append('＞');
                    return;
                }
            }
            sb.append(s.charAt(index));
        }
        /**
         * 获取最原始的request
         *
         * @return
         */
        public HttpServletRequest getOrgRequest() {
            return orgRequest;
        }
        /**
         * 获取最原始的request的静态方法
         *
         * @return
         */
        public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
            if (req instanceof XssProtection) {
                return ((XssProtection) req).getOrgRequest();
            }
            return req;
        }
}
