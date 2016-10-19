package io.demor.nuts.common.server;

import android.content.res.AssetManager;
import com.x5.template.providers.TemplateProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class AssetTemplateLoader extends TemplateProvider {

    final AssetManager mAssetManager;

    public AssetTemplateLoader(final AssetManager assetManager) {
        mAssetManager = assetManager;
    }

    @Override
    public String getProtocol() {
        return "android";
    }

    @Override
    public String loadContainerDoc(final String docName) throws IOException {
        final InputStream in = mAssetManager.open("web/temp-" + docName.replaceAll(".chtml", "") + ".html");
        try {
            final Scanner scanner = new Scanner(in);
            final StringBuilder result = new StringBuilder();
            while (scanner.hasNextLine()) {
                result.append(scanner.nextLine())
                        .append('\n');
            }
            return result.toString();
        } finally {
            in.close();
        }
    }
}