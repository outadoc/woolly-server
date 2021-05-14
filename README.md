# woolly-auth-proxy

Mastodon authentication middleware server.

## Description

On Twitter, you could just register your app on their website, get a `client_id` and `client_secret`, and embed those in
your app to authenticate your users.

Mastodon being a decentralized network, OAuth clients cannot be setup once and for all; this app setup process can be
automated, and should be done once per instance.

This necessitates a central server that will proxy the app's OAuth-related requests to the proper instance and add the
proper `client_id` and `client_secret` to calls, registering the app automatically if needed, or picking from its
database of already known instances.

## Configuration

Run this as a standard Java application. The settings JSON file will be loaded from `/etc/woolly/server_config.json` by
default, but this path can be overridden with the `WOOLLY_CONFIG_PATH` environment variable.

The JSON file should be structure like this:

```json
{
  "application": {
    "name": "Woolly",
    "website": "https://woolly.app",
    "scopes": [
      "read",
      "write",
      "follow",
      "push"
    ],
    "redirectUris": [
      "urn:ietf:wg:oauth:2.0:oob",
      "woolly://oauth/callback",
      "https://woolly.app/oauth/callback"
    ]
  },
  "mysql": {
    "connectionString": "jdbc:mysql://localhost:3306/test",
    "username": "root",
    "password": "root"
  }
}
```
