var webpack = require("webpack");

module.exports = {
  module: {
    loaders: [
      { test: /\.js$/, exclude: /node_modules/, loader: 'babel-loader?presets[]=es2015&presets[]=react' }
    ]
  },

  devServer: {
    historyApiFallback: true,
    proxy: {
      '/Api/*': {
        target: 'http://localhost:8998'
      },
      '/web/*': {
        target: 'http://localhost:8080',
        pathRewrite: {'^/web' : ''}
      },
    }
  },

  plugins:[
    new webpack.DefinePlugin({
      'process.env':{
        'NODE_ENV': JSON.stringify('production')
      }
    })
  ]
  
}