/**
 * Doom Tooltip jQuery Plugin
 *
 * @author Dumitru Glavan
 * @link http://dumitruglavan.com
 * @version 1.3 (10-SEP-2011)
 * @requires jQuery v1.3.2 or later
 *
 * @example: $('#tip-pointer').doomTooltip({
*    			tipId:'#doom-tooltip-auto',
*				headerText: 'Doom Tooltip Header',
*				text:'Great success from the dark side content text :)',
*				footerText: 'Doom Tooltip Footer',
*				hideTime:2000
*			});
 * @example: $('#tip-pointer').doomTooltip({
*				tipId:'#doom-tooltip-click',
*				headerText: 'Doom Tooltip Header',
*				text:'Great success from the dark side content text :)',
*				footerText: 'Doom Tooltip Footer',
*				hideTime:false,
*				hideOnClick:true
*			});
 *
 * Examples and documentation at: https://github.com/doomhz/jQuery-Doom-Tooltip
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 */
(function ($) {
    $.fn.doomTooltip = function (options) {
        var self = this;
        var $self = $(this);

        if ($self.length > 1) {
            $self.each(function (i, el) {
                $(el).doomTooltip(options);
            });
            return this;
        }

        this.config = {
            tipId: '',
            extraClass: '',
            text: '',
            position: 'bottom',
            showOnHover: true,
            hideOnOut: true,
            autoShow: false,
            autoRemove: false,
            autoHideTime: 0,
            fadeTime: 0,
            hideOnClick: false,
            style: {'position': 'absolute', 'z-index': '1000'},
            topMargin: 5,
            leftMargin: 0,
            headerText: '',
            footerText: '',
            tooltipHtml: '<div class="doom-tooltip-cnt" id="{tipId}">\n\
							<div >{tipHeaderText}</div>\n\
							<div class="doom-tooltip-text">{tipText}</div>\n\
							<div class="doom-tooltip-footer">{tipFooterText}</div>\n\
						</div>',
            afterHide: null,
            offsetOnShow: false,
            textOnShow: false
        };
        this.config = $.extend(this.config, options);
        this.config.text = this.config.text === '' && $self.attr('data-tooltip-text') ? $self.attr('data-tooltip-text') : this.config.text;
        this.config.headerText = this.config.headerText === '' && $self.attr('data-tooltip-headerText') ? $self.attr('data-tooltip-headerText') : this.config.headerText;
        this.config.footerText = this.config.footerText === '' && $self.attr('data-tooltip-footerText') ? $self.attr('data-tooltip-footerText') : this.config.footerText;
        this.config.tipId = !this.config.tipId ? 'tool-tip-' + new Date().getTime() : this.config.tipId.replace('#', '');

        var $existentTip = $('#' + this.config.tipId);

        if ($existentTip.length) {
            this.tipContainer = $existentTip;
        } else {
            this.tipContainer = $(this.config.tooltipHtml
                .replace('{tipId}', this.config.tipId)
                .replace('{tipHeaderText}', this.config.headerText)
                .replace('{tipFooterText}', this.config.footerText)
                .replace('{tipText}', this.config.text))
                .css(this.config.style)
                .addClass(this.config.extraClass)
                .hide()
                .prependTo('body');
        }

        this.setOffset();

        if (this.config.autoShow) {
            this.showTooltip();
        }

        if (this.config.autoHideTime) {
            setTimeout(function () {
                self.hideTooltip();
            }, self.config.autoHideTime);
        }

        if (this.config.hideOnClick) {
            this.tipContainer.click(function () {
                self.hideTooltip();
            });
        }

        return this;
    },

        $.fn.showTooltip = function () {
            var $self = $(this);
            if (this.config.textOnShow) {
                $(this.tipContainer).find('div.doom-tooltip-text:first').html($self.attr('data-tooltip-text'));
            }
            if (this.config.offsetOnShow) {
                this.setOffset.call(this);
            }
            $(this.tipContainer).show().fadeTo(this.config.fadeTime, 1);
        },

        $.fn.hideTooltip = function () {
            var self = this
            $(this.tipContainer).fadeTo(this.config.fadeTime, 0, function () {
                $.isFunction(self.config.afterHide) && self.config.afterHide.call(self.tipContainer);
                self.config.autoRemove && self.tipContainer.remove();
                self.tipContainer.hide();
            });
        },

        $.fn.setOffset = function () {
            var $self = $(this), pointerOffset = $self.offset(), left = 0, top = 0;
            console.log($self);
            switch (this.config.position) {
                case 'left':
                    left = pointerOffset.left - this.tipContainer.width() + this.config.leftMargin;
                    top = pointerOffset.top + parseInt($self.height() / 2) - (this.tipContainer.height() / 2) + this.config.topMargin;
                    break;
                case 'right':
                    left = pointerOffset.left + parseInt($self.width()) + this.config.leftMargin;
                    top = pointerOffset.top + parseInt($self.height() / 2) + this.config.topMargin;
                    break;
                case 'top':
                    left = (pointerOffset.left + parseInt($self.width() / 2) - parseInt(this.tipContainer.width() / 2)) + this.config.leftMargin;
                    top = pointerOffset.top + this.config.topMargin - this.tipContainer.height();
                    break;
                default:
                    left = (pointerOffset.left + parseInt($self.width() / 2) - parseInt(this.tipContainer.width() / 2)) + this.config.leftMargin;
                    top = pointerOffset.top + parseInt($(this).height()) + this.config.topMargin;
                    break;
            }

            pointerOffset = {
                top: top,
                left: left
            };
            this.tipContainer.css(pointerOffset);
        }

})(jQuery);