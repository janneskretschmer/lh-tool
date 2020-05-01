var path = require('path');
var webpack = require('webpack');
var TerserPlugin = require('terser-webpack-plugin');
var settings = require('./src/main/js/settings');

var PROD_BUILD = settings.prodBuild;

module.exports = {
	entry: './src/main/js/browser.js',
	devtool: PROD_BUILD ? 'cheap-module-source-map' : 'sourcemaps',
	cache: true,
	mode: PROD_BUILD ? 'production' : 'development',
	output: {
		path: __dirname,
		filename: './src/main/resources/static/built/bundle.js'
	},
	plugins: PROD_BUILD ? [
		new webpack.DefinePlugin({
			'process.env': {
				'NODE_ENV': JSON.stringify('production')
			}
		})
	] : [],
	optimization: PROD_BUILD ? {
		minimizer: [
			new TerserPlugin({
				cache: true,
				parallel: true,
				terserOptions: {
					compress: true,
					ecma: 6,
					mangle: true
				},
				sourceMap: false
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
				loader: 'babel-loader',
				options: {
					presets: ['@babel/preset-env', '@babel/preset-react'],
					plugins: [
						['@babel/plugin-proposal-decorators', { legacy: true }],
						['@babel/plugin-proposal-class-properties', { loose: true }],
					],
				}
			}]
		}]
	}
};