const path = require('path');
const webpack = require('webpack');
const TerserPlugin = require('terser-webpack-plugin');

const settings = require('./src/main/js/settings');
const PROD_BUILD = settings.prodBuild;
const ENVIRONMENT = PROD_BUILD ? 'production' : 'development';

module.exports = {
	entry: './src/main/js/browser.js',
	devtool: PROD_BUILD ? false : 'inline-source-map',
	cache: true,
	mode: ENVIRONMENT,
	output: {
		path: __dirname,
		filename: './src/main/resources/static/built/bundle.js'
	},
	plugins: [
		new webpack.DefinePlugin({
			'process.env.NODE_ENV': JSON.stringify(ENVIRONMENT),
		}),
		new webpack.ProvidePlugin({
			process: 'process/browser',
		}),
	],
	optimization: PROD_BUILD ? {
		minimizer: [
			new TerserPlugin({
				parallel: true,
				terserOptions: {
					compress: true,
					ecma: 6,
					mangle: true
				},
			})
		]
	} : {},
	module: {
		rules: [{
			test: path.join(__dirname, '.'),
			include: [
				path.resolve(__dirname, 'src'),
				path.resolve(__dirname, 'node_modules/superagent')
			],
			use: [{
				loader: 'ts-loader',
				/*options: {
					presets: ['@babel/preset-env', '@babel/preset-react'],
					plugins: [
						['@babel/plugin-proposal-decorators', { legacy: true }],
						['@babel/plugin-proposal-class-properties', { loose: true }],
					],
				}*/
			}]
		}, {
			enforce: 'pre',
			test: /\.js$/,
			exclude: /node_modules/,
			loader: 'source-map-loader'
		}]
	},
	resolve: {
		extensions: ['.ts', '.tsx', '.js', '.jsx'],
		fallback: {
			buffer: require.resolve('buffer/'),
			crypto: require.resolve('crypto-browserify'),
			util: require.resolve('util/'),
			stream: require.resolve('stream-browserify'),
		}
	}
};