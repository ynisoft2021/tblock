#
# Copyright (C) u-blox
#
# For additional copyright information see the LICENSE file in the root of this meta layer.
#

PR = "r0"
SUMMARY = "u-blox VERA-P3 SDIO driver kernel module"

LICENSE = "u-blox-proprietary"
LIC_FILES_CHKSUM = " \
    file://${WORKDIR}/u-blox/p3-v${PV}/COPYING;md5=c82f103c7edfae81aec6ae00ebb58f13 \
"

S = "${WORKDIR}/u-blox/p3-v${PV}/src/p3-sdio-mod/"

SRC_URI = "\
    ${LULA_DL_URL}u-blox-p3_v${PV}_armv7ahf-neon.tar.gz \
    file://vera-p3-driver-sdio.conf \
    file://0001-include-header-for-allow-signal.patch \
"
SRC_URI[md5sum] = "d36c91e6470097b4bbc5027b6732dfd6"
SRC_URI[sha256sum] = "60904ecb81e1a1d305458f3e01b8feccf20de5e97b71d60d41948b0b0f5a1d0e"

inherit module deploy

RDEPENDS_${PN} += "vera-p3-firmware"

MOD_DIR = "${D}/lib/modules/${KERNEL_VERSION}/updates/u-blox/vera-p3/"
CONF_DIR = "${D}${sysconfdir}/modprobe.d/"

do_install() {
    install -d ${MOD_DIR}
    install -m 755 ${S}/p3-sdio.ko ${MOD_DIR}

    install -d ${CONF_DIR}
    install -m 0644 ${WORKDIR}/vera-p3-driver-sdio.conf ${CONF_DIR}
}

FILES_${PN} += "${sysconfdir}/modprobe.d/vera-p3-driver-sdio.conf"
