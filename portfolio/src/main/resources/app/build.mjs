/**
 * Build Script for the frontend app.
 *
 * Based on those found in https://github.com/tfinlay/tankcosc (MIT License)
 */
import fs from "fs";
import esbuild from "esbuild";
import cssModulesPlugin from "esbuild-css-modules-plugin";

const targetDirectory = "../static/app";

// Delete ../static/app/ if it exists
if (fs.existsSync(targetDirectory)) {
    fs.rmdirSync(targetDirectory, { recursive: true });
}

// Build Project
await esbuild.build({
    entryPoints: [
        "./src/entry/monthly_planner.tsx"
    ],
    outdir: targetDirectory,
    bundle: true,
    platform: "browser",
    sourcemap: "linked",
    plugins: [
        cssModulesPlugin()
    ]
});