# CI/CD Setup Guide

This document describes how to set up the CI/CD pipeline for SSDL.

## Required GitHub Secrets

To enable publishing to Sonatype, you need to set up the following secrets in your GitHub repository:

### Sonatype Credentials
1. `SONATYPE_USERNAME` - Your Sonatype JIRA username
2. `SONATYPE_PASSWORD` - Your Sonatype JIRA password

### PGP Signing
1. `PGP_SECRET` - Your PGP private key in ASCII armor format
2. `PGP_PASSPHRASE` - The passphrase for your PGP key

## Setting up Sonatype Account

1. Create a Sonatype JIRA account at https://issues.sonatype.org/
2. Create a new project ticket to register your group ID (`io.github.zrhmn`)
3. Follow the instructions in the Sonatype documentation

## Setting up PGP Keys

```bash
# Generate a new PGP key
gpg --gen-key

# Export the private key (replace YOUR_KEY_ID with actual key ID)
gpg --armor --export-secret-keys YOUR_KEY_ID

# Export the public key
gpg --armor --export YOUR_KEY_ID
```

## Publishing Process

### Snapshot Publishing
- Automatically publishes SNAPSHOT versions on every push to `main` branch
- Uses dynamic versioning based on git commits

### Release Publishing
- Triggered by pushing a git tag starting with `v` (e.g., `v1.0.0`)
- Creates a GitHub release
- Publishes to Maven Central via Sonatype

### Manual Release
```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0
```

## Workflows

- **CI (`ci.yml`)** - Runs tests, compilation, and code formatting checks
- **Release (`release.yml`)** - Publishes releases to Sonatype and creates GitHub releases
- **Snapshot (`snapshot.yml`)** - Publishes snapshot versions from main branch

## Dependencies

- **Dependabot** - Automatically updates GitHub Actions and project dependencies
