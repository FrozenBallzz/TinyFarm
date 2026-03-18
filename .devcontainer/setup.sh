#!/bin/bash
set -e  # Stop if any command fails

# Ensure SDKMAN is loaded before using it
#source ~/.sdkman/bin/sdkman-init.sh

# Install Java
#sdk install java 17.0.8-tem

# faster to just move the old file, since the new one is not working
sudo mv /etc/apt/sources.list.d/yarn.list /etc/apt/sources.list.d/yarn.list.orig

# Install Playwright and Chromium browser
pip install playwright
playwright install chromium
