var path = require('path');
var webpack = require('webpack');
var TerserPlugin = require('terser-webpack-plugin');
var settings = require('./src/main/js/settings');

var PROD_BUILD = settings.prodBuild;

var browserConfig = {
	entry: {
		browser: './src/main/js/browser.js',
	},
	devtool: PROD_BUILD ? 'cheap-module-source-map' : 'sourcemaps',
	target: 'web',
	cache: true,
	mode: PROD_BUILD ? 'production' : 'development',
	output: {
		path: __dirname + '/src/main/resources/static/built/',
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
				uglifyOptions: {
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

var serverConfig = {
	entry: {
		server: './src/main/js/server.js',
	},
	devtool: PROD_BUILD ? 'cheap-module-source-map' : 'sourcemaps',
	target: 'node',
	cache: true,
	mode: PROD_BUILD ? 'production' : 'development',
	output: {
		path: __dirname + '/src/main/resources/static/built/',
		library: 'lhtool',
		libraryTarget: 'umd',
	},
	node: {
		__dirname: true,
	},
	plugins: PROD_BUILD ? [
		new webpack.DefinePlugin({
			'process.env': {
				'NODE_ENV': JSON.stringify('production')
			}
		}),
		new webpack.DefinePlugin({ 'global.GENTLY': false }),
	] : [
		new webpack.DefinePlugin({ 'global.GENTLY': false }),
	],
	optimization: PROD_BUILD ? {
		minimizer: [
			new TerserPlugin({
				cache: true,
				parallel: true,
				uglifyOptions: {
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

module.exports = [ serverConfig, browserConfig ];