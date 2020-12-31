'use strict'
const { VueLoaderPlugin } = require('vue-loader')
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const CopyWebpackPlugin = require('copy-webpack-plugin');
const ImageminPlugin = require('imagemin-webpack-plugin').default;
const path = require('path');
const webpack = require('webpack')

module.exports = {
    entry: [
        path.resolve(__dirname, 'src/main/resources/resource/app.js'),
        path.resolve(__dirname, 'src/main/resources/resource/app.scss'),
    ],
    target: 'web',
    output: {
        path: path.resolve(__dirname, 'src/main/resources/static/public'),
        filename: 'js/[name].js',
        chunkFilename: 'js/[id].[chunkhash].js',
        publicPath: '/public/',
        pathinfo: false,
    },
    module: {
        rules: [
            {
                test: /\.vue([\?a-z0-9&=.]+)?$/,
                use: [
                    'vue-loader',
                ],
                exclude: /node_modules/,
            },
            {
                test: /\.(scss|sass|css)$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    "css-loader",
                    // Add prefixes automatically
                    'postcss-loader',
                    // Compiles Sass to CSS
                    'sass-loader',
                ],
            },
            {
                test: /\.(jpe?g|png|gif|svg)(\?[a-z0-9=.]+)?$/,
                include: /node_modules/,
                loader: 'file-loader',
                options: {
                    name: 'img/[name].[hash].[ext]',
                }
            },
            {
                test: /\.(ttf|eot|woff|woff2)(\?[a-z0-9=.]+)?$/,
                include: /node_modules/,
                loader: 'file-loader',
                options: {
                    name: 'fonts/[name].[hash].[ext]',
                }
            },
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                include: path.resolve(__dirname, 'src/main/resources/resource'),
                loader: "babel-loader",
                options: {
                    presets: [
                        'vue',
                        [
                            '@babel/preset-env',
                            {
                                "targets": {
                                    "browsers": [
                                        "last 2 versions",
                                        'not ie > 0',
                                    ]
                                },
                                "useBuiltIns": "usage",
                                "corejs": { "version": 3, "proposals": false }
                            },
                            
                        ]
                    ],
                    plugins: [
                        ["@babel/plugin-transform-runtime"],
                        ["@babel/plugin-proposal-class-properties", { "loose": true }]
                    ],
                    cacheDirectory: true,
                },
            },
            {
                test: /\.(ts|tsx)$/,
                loader: "ts-loader",
                exclude: /node_modules/,
                options: {
                    transpileOnly: true
                },
            },
        ]
    },
    resolve: {
        extensions: ['.js', '.vue', '.jsx', '.ts', '.tsx'],
        alias: {
            '@': path.resolve(__dirname, 'src/main/resources/resource'),
            "path": require.resolve("path-browserify"),
            "crypto": require.resolve("crypto-browserify"),
            "querystring": require.resolve("querystring-es3"),
            "buffer": require.resolve("buffer"),
            "stream": require.resolve("stream-browserify")
        }
    },
    optimization: {
        minimize: false,
        moduleIds: 'deterministic',
        runtimeChunk: 'single',
        removeAvailableModules: false,
        removeEmptyChunks: false,
        splitChunks: {
            chunks: "async",
            minSize: 1000,
            minChunks: 2,
            maxAsyncRequests: 50,
            maxInitialRequests: 10,
            cacheGroups: {
                commons: {
                    test: /[\\/]node_modules[\\/]/,
                    name: 'vendors',
                    chunks: 'all'
                }
            }
        }
    },
    plugins: [
        new webpack.HotModuleReplacementPlugin(),
        new VueLoaderPlugin(),
        new MiniCssExtractPlugin({
            filename: 'css/[name].css',
            chunkFilename: 'css/[name].[id].css',
        }),
        new webpack.ProvidePlugin({
            process: 'process/browser',
        }),
        new CopyWebpackPlugin({
            patterns: [
                { 
                    from: path.resolve(__dirname, 'src/main/resources/resource/img'),
                    to: path.resolve(__dirname, 'src/main/resources/static/public/img'),
                    force: true,
                }
            ]
        }),
        new ImageminPlugin(),
    ],
    devServer: {
        watchOptions: {
            ignored: [
                /node_modules/,
            ],
            compress: true,
            mode: 'development',
            devtool: 'cheap-source-map',
            hot: true,
            stats: "errors-only",
            open: true,
            port: 3000,
        }
    },
};

if (process.env.NODE_ENV === 'production') {
    const TerserWebpackPlugin = require('terser-webpack-plugin');
    const OptimizeCssPlugin = require('optimize-css-assets-webpack-plugin');

    module.exports.devtool = false;
    module.exports.optimization.removeAvailableModules = true;
    module.exports.optimization.removeEmptyChunks = true;
    module.exports.optimization.minimize = true;
    module.exports.optimization.minimizer = [
        // Optimize javascript code
        new TerserWebpackPlugin({
            terserOptions: {
                // Remove console from the code
                compress: { drop_console: true }
            },
        }),
        // Optimzie css code
        new OptimizeCssPlugin(),
    ],
    // http://vue-loader.vuejs.org/en/workflow/production.html
    module.exports.plugins = (module.exports.plugins || []).concat([
        new webpack.DefinePlugin({
            'process.env': {
                NODE_ENV: '"production"'
            }
        }),
        new webpack.LoaderOptionsPlugin({
            minimize: true
        }),
        new CleanWebpackPlugin({
            cleanOnceBeforeBuildPatterns: ['**/*', '!robot.txt'],
        }),
    ])
}