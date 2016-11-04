package io.demor.nuts.lib.net;

import com.squareup.okhttp.*;
import io.demor.nuts.lib.log.L;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.Charset;

public class CurlLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private String curlOptions;

    /**
     * Set any additional curl command options (see 'curl --help').
     */
    public void setCurlOptions(String curlOptions) {
        this.curlOptions = curlOptions;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        boolean compressed = false;

        StringBuilder curlCmd = new StringBuilder("curl");
        if (curlOptions != null) {
            curlCmd.append(" ").append(curlOptions);
        }
        curlCmd.append(" -X ").append(request.method());

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            if ("Accept-Encoding".equalsIgnoreCase(name) && "gzip".equalsIgnoreCase(value)) {
                compressed = true;
            }
            curlCmd.append(" -H ").append("\"").append(name).append(": ").append(value).append("\"");
        }

        RequestBody requestBody = request.body();
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            // try to keep to a single line and use a subshell to preserve any line breaks
            curlCmd.append(" --data $'").append(buffer.readString(charset).replace("\n", "\\n")).append("'");
        }

        curlCmd.append((compressed) ? " --compressed " : " ").append(request.url());

        L.d("╭--- cURL (%s)", request.url());
        L.d(curlCmd.toString());
        L.d("╰--- (copy and paste the above line to a terminal)");

        return chain.proceed(request);
    }

}
